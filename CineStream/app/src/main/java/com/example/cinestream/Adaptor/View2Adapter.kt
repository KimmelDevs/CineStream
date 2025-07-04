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
import com.example.cinestream.Helper.Movie
import com.example.cinestream.R

class View2Adapter(private val movies: MutableList<Movie>) : RecyclerView.Adapter<View2Adapter.MovieViewHolder>() {

    fun updateMovies(newMovies: List<Movie>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_moviess, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val poster: ImageView = view.findViewById(R.id.moviePoster)
        private val title: TextView = view.findViewById(R.id.movieTitle)
        private val year: TextView = view.findViewById(R.id.movieYear)
        private val rating: TextView = view.findViewById(R.id.movieRating)

        fun bind(movie: Movie) {
            // Load the movie poster using Glide
            Glide.with(itemView.context)
                .load("https://image.tmdb.org/t/p/w500${movie.poster_path}")
                .into(poster)

            // Set the movie title
            title.text = movie.title

            // Set the movie release year (assuming release_date is in "YYYY-MM-DD" format)
            year.text = movie.release_date?.split("-")?.get(0) // Get the year part

            // Set the movie rating (assuming vote_average is a float between 0-10)
            rating.text = "${movie.vote_average}"

            // Set up click listener for the movie item
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, MovieDetailsActivity::class.java)

                intent.putExtra("MOVIE_ID", movie.id)
                intent.putExtra("MOVIE_TITLE", movie.title)
                intent.putExtra("MOVIE_OVERVIEW", movie.overview)
                intent.putExtra("MOVIE_POSTER", movie.poster_path)
                intent.putExtra("MOVIE_RELEASE_DATE", movie.release_date)
                intent.putExtra("MOVIE_VOTE_AVERAGE", movie.vote_average)
                intent.putExtra("MOVIE_VOTE_COUNT", movie.vote_count)
                intent.putExtra("MOVIE_POPULARITY", movie.popularity)
                intent.putExtra(
                    "MOVIE_GENRE_IDS",
                    movie.genre_ids?.toIntArray() // Convert List to IntArray
                )
                intent.putExtra("MOVIE_BACKDROP_PATH", movie.backdrop_path)
                intent.putExtra("MOVIE_ORIGINAL_TITLE", movie.original_title)
                intent.putExtra("MOVIE_ORIGINAL_LANGUAGE", movie.original_language)
                intent.putExtra("MOVIE_ADULT", movie.adult)
                intent.putExtra("MOVIE_VIDEO", movie.video)
                itemView.context.startActivity(intent)
            }
        }
    }
}
