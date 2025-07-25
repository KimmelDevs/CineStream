package com.example.cinestream.Adaptor

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinestream.Activities.MovieDetailsActivity
import com.example.cinestream.R
import com.example.cinestream.Helper.Movie

class FavoriteMoviesAdapter(private val movies: List<Movie>) :
    RecyclerView.Adapter<FavoriteMoviesAdapter.MovieViewHolder>() {
    private val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Science Fiction",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.findViewById(R.id.moviePoster)
        val title: TextView = view.findViewById(R.id.movieTitle)
        val releaseDate: TextView = view.findViewById(R.id.movieReleaseDate)
        val genres: TextView = view.findViewById(R.id.movieGenres)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        val genres = movie.genre_ids?.joinToString(", ") { id ->
            genreMap[id] ?: "Unknown Genre" // Use the genre name if it exists, otherwise "Unknown Genre"
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, MovieDetailsActivity::class.java).apply {
                putExtra("MOVIE_ID", movie.id)
                putExtra("MOVIE_TITLE", movie.title)
                putExtra("MOVIE_OVERVIEW", movie.overview)
                putExtra("MOVIE_POSTER", movie.poster_path)
                putExtra("MOVIE_RELEASE_DATE", movie.release_date)
                putExtra("MOVIE_VOTE_AVERAGE", movie.vote_average)
                putExtra("MOVIE_VOTE_COUNT", movie.vote_count)
                putExtra("MOVIE_POPULARITY", movie.popularity)
                putExtra("MOVIE_GENRE_IDS", movie.genre_ids?.toIntArray()) // Send genre IDs as array
                putExtra("MOVIE_BACKDROP_PATH", movie.backdrop_path)
                putExtra("MOVIE_ORIGINAL_TITLE", movie.original_title)
                putExtra("MOVIE_ORIGINAL_LANGUAGE", movie.original_language)
                putExtra("MOVIE_ADULT", movie.adult)
                putExtra("MOVIE_VIDEO", movie.video)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.genres.text = genres
        holder.title.text = movie.title
        holder.releaseDate.text = movie.overview
        Glide.with(holder.itemView.context)
            .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
            .into(holder.poster)
    }

    override fun getItemCount(): Int = movies.size
}
