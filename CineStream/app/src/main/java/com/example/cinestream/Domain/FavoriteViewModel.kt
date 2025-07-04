package com.example.cinestream.Domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cinestream.Helper.Movie
import com.example.cinestream.Helper.MovieApi
import androidx.lifecycle.viewModelScope

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val apiService: MovieApi
) : ViewModel() {

    private val _favoriteMovies = MutableLiveData<List<Movie>>()
    val favoriteMovies: LiveData<List<Movie>> get() = _favoriteMovies

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadFavorites() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userDocRef = firestore.collection("users").document(currentUser.uid)
            userDocRef.addSnapshotListener { documentSnapshot, e ->
                if (e != null) {
                    _errorMessage.value = "Failed to load favorites: ${e.message}"
                    return@addSnapshotListener
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val favorites = documentSnapshot["favorites"] as? List<Long>
                    if (favorites != null && favorites.isNotEmpty()) {
                        fetchFavoriteMovies(favorites)
                    } else {
                        _errorMessage.value = "No favorites found!"
                    }
                }
            }
        } else {
            _errorMessage.value = "You must be logged in to view favorites!"
        }
    }

    private fun fetchFavoriteMovies(favorites: List<Long>) {
        viewModelScope.launch(Dispatchers.IO) {
            val movieDetailsList = mutableListOf<Movie>()
            try {
                for (movieIdLong in favorites) {
                    val movieId = movieIdLong.toInt()
                    val response = apiService.getMovieDetails(movieId, "1002b252d6774f7c3dbd855cf7d3e459")
                    if (response.isSuccessful && response.body() != null) {
                        val movieDetails = response.body()!!
                        val movie = Movie(
                            id = movieDetails.id,
                            title = movieDetails.title,
                            overview = movieDetails.overview,
                            poster_path = movieDetails.poster_path,
                            release_date = movieDetails.release_date,
                            vote_average = movieDetails.vote_average,
                            vote_count = movieDetails.vote_count,
                            popularity = movieDetails.popularity,
                            genre_ids = movieDetails.genre_ids,
                            backdrop_path = movieDetails.backdrop_path,
                            original_title = movieDetails.original_title,
                            original_language = movieDetails.original_language,
                            adult = movieDetails.adult,
                            video = movieDetails.video,
                            external_links = movieDetails.external_links
                        )
                        movieDetailsList.add(movie)
                    }
                }
                withContext(Dispatchers.Main) {
                    _favoriteMovies.value = movieDetailsList
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Error: ${e.message}"
                }
            }
        }
    }
}