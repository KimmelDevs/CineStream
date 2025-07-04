package com.example.cinestream.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinestream.Adaptor.FavoriteMoviesAdapter
import com.example.cinestream.Helper.Movie
import com.example.cinestream.Helper.MovieApi
import com.example.cinestream.Helper.RetrofittInstance
import com.example.cinestream.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteMoviesAdapter

    private lateinit var apiService: MovieApi // This is the interface for your API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)

        // Initialize Firebase
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Retrofit for the movie API (TMDB in this case)
        apiService = RetrofittInstance.api // Uses the RetrofittInstance for TMDB API

        // Load favorites
        loadFavorites()
    }
    private fun loadFavorites() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    // Cast the favorites as List<Long> instead of List<String>
                    val favorites =
                        document["favorites"] as? List<Long> // Get the list of favorite movie IDs as Long
                    if (favorites != null && favorites.isNotEmpty()) {
                        fetchFavoriteMovies(favorites)
                    } else {
                        Toast.makeText(this, "No favorites found!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to load favorites: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(this, "You must be logged in to view favorites!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun fetchFavoriteMovies(favorites: List<Long>) {
        // Using a Coroutine to fetch movie details
        CoroutineScope(Dispatchers.Main).launch {
            val movieDetailsList = mutableListOf<Movie>()
            try {
                // Loop through the favorite movie IDs (now as Long)
                for (movieIdLong in favorites) {
                    // Convert the Long to Int (you can cast Long to Int directly if it fits within Int range)
                    val movieId = movieIdLong.toInt() // Use toInt() or check the range if necessary

                    val response = apiService.getMovieDetails(
                        movieId,
                        "1002b252d6774f7c3dbd855cf7d3e459"
                    ) // Replace with your actual TMDB API key
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
                            video = movieDetails.video
                        )

                        movieDetailsList.add(movie)
                    } else {
                        // Handle error or missing movie details
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@FavoritesActivity,
                                "Failed to fetch movie details for $movieId",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                // Notify the adapter once all movies are fetched
                if (movieDetailsList.isNotEmpty()) {
                    adapter = FavoriteMoviesAdapter(movieDetailsList)
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(this@FavoritesActivity, "No movies found", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@FavoritesActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}