package com.example.cinestream.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cinestream.R
import com.example.cinestream.databinding.ActivityMovieDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MovieDetailsActivity : AppCompatActivity() {
    private lateinit var favoriteIds: MutableList<Int> // Local list to store favorite IDs
    private lateinit var binding: ActivityMovieDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var isFavorite = false // To track the favorite status of the movie

    private val genreMap = mapOf(
        28 to "Action", 12 to "Adventure", 16 to "Animation", 35 to "Comedy",
        80 to "Crime", 99 to "Documentary", 18 to "Drama", 10751 to "Family",
        14 to "Fantasy", 36 to "History", 27 to "Horror", 10402 to "Music",
        9648 to "Mystery", 10749 to "Romance", 878 to "Science Fiction",
        10770 to "TV Movie", 53 to "Thriller", 10752 to "War", 37 to "Western"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Retrieve data from Intent
        val title = intent.getStringExtra("MOVIE_TITLE")
        val posterPath = intent.getStringExtra("MOVIE_POSTER")
        val backdropPath = intent.getStringExtra("MOVIE_BACKDROP_PATH")
        val overview = intent.getStringExtra("MOVIE_OVERVIEW")
        val releaseDate = intent.getStringExtra("MOVIE_RELEASE_DATE")
        val originalTitle = intent.getStringExtra("MOVIE_ORIGINAL_TITLE")
        val popularity = intent.getDoubleExtra("MOVIE_POPULARITY", 0.0)
        val voteAverage = intent.getDoubleExtra("MOVIE_VOTE_AVERAGE", 0.0)
        val genreIds = intent.getIntArrayExtra("MOVIE_GENRE_IDS")?.toList() ?: emptyList()
        val movieId = intent.getIntExtra("MOVIE_ID", -1)

        // Bind data to views
        binding.movieTitle.text = title
        binding.movieOverview.text = overview
        binding.movieReleaseDate.text = "Release Date: $releaseDate"
        binding.movieOriginalTitle.text = "Original Title: $originalTitle"
        binding.movieVoteAverage.text = "Rating: $voteAverage / 10"
        binding.moviePopularity.text = "Popularity: $popularity"

        // Map genre IDs to genre names
        val genres = genreIds.joinToString(", ") { id ->
            genreMap[id] ?: "Unknown Genre"
        }
        binding.movieGenres.text = "Genres: $genres"

        // Load images using Glide
        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500$posterPath")
            .into(binding.moviePoster)

        Glide.with(this)
            .load("https://image.tmdb.org/t/p/w500$backdropPath")
            .into(binding.movieBackdrop)

        // Back button listener
        binding.icBack.setOnClickListener { finish() }

        // Real-time favorite listener
        setupFavoriteListener(movieId)

        // Favorite button listener
        binding.icHeart.setOnClickListener {
            toggleFavorite(movieId)
        }
    }

    private fun setupFavoriteListener(movieId: Int) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userDocRef = firestore.collection("users").document(currentUser.uid)

            userDocRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(
                        this,
                        "Failed to load favorites: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    // Fetch favorites as a list of integers and store locally
                    val favorites = snapshot.get("favorites") as? List<*> ?: emptyList<Any>()
                    favoriteIds = favorites.mapNotNull { it as? Long }.map { it.toInt() }.toMutableList()

                    // Check if the current movie ID is in the local favorites list
                    isFavorite = favoriteIds.contains(movieId)

                    // Update heart icon color based on favorite status
                    updateHeartIcon(isFavorite)
                }
            }
        }
    }

    private fun toggleFavorite(movieId: Int) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userDocRef = firestore.collection("users").document(currentUser.uid)

            if (isFavorite) {
                // Remove the movie from favorites
                userDocRef.update(
                    "favorites",
                    com.google.firebase.firestore.FieldValue.arrayRemove(movieId)
                )
                    .addOnSuccessListener {
                        Toast.makeText(this, "Movie removed from favorites!", Toast.LENGTH_SHORT)
                            .show()
                        favoriteIds.remove(movieId) // Update the local list
                        updateHeartIcon(false) // Update UI
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to remove favorite: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                // Add the movie to favorites
                userDocRef.update(
                    "favorites",
                    com.google.firebase.firestore.FieldValue.arrayUnion(movieId)
                )
                    .addOnSuccessListener {
                        Toast.makeText(this, "Movie added to favorites!", Toast.LENGTH_SHORT).show()
                        favoriteIds.add(movieId) // Update the local list
                        updateHeartIcon(true) // Update UI
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to add favorite: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        } else {
            Toast.makeText(this, "You must be logged in to modify favorites!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Helper function to update heart icon
    private fun updateHeartIcon(isFavorite: Boolean) {
        val heartColor = if (isFavorite) R.color.red else R.color.black
        binding.icHeart.setColorFilter(resources.getColor(heartColor, theme))
    }
}
