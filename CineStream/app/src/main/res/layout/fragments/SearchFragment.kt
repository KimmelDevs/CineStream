package com.example.cinestream.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinestream.Adaptor.View2Adapter
import com.example.cinestream.Helper.Movie
import com.example.cinestream.Helper.MovieApi
import com.example.cinestream.R
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment() {

    private lateinit var movieApi: MovieApi
    private lateinit var movieAdapter: View2Adapter
    private var moviesList: MutableList<Movie> = mutableListOf()

    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBarrer: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/") // Replace with actual base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        movieApi = retrofit.create(MovieApi::class.java) // Initialize movieApi


        // Set up RecyclerView
        val backarrow = view.findViewById<ImageView>(R.id.backArrow)

        // Set the click listener
        backarrow.setOnClickListener {
            val fragmentManager = parentFragmentManager
            val transaction = fragmentManager.beginTransaction()

            // Replace the current fragment with the SearchFragment
            transaction.replace(R.id.fragmentContainer, HomeFragment())
            transaction.addToBackStack(null) // Adds this transaction to the back stack
            transaction.commit()
        }
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        movieAdapter = View2Adapter(moviesList)
        recyclerView.adapter = movieAdapter

        // Set up progress bar

        progressBarrer = view.findViewById(R.id.progressBarrer)
        progressBarrer.visibility = View.VISIBLE // Show progress bar initially

        // Set up search EditText with TextWatcher
        searchEditText = view.findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener { charSequence ->
            if (charSequence.isNullOrEmpty()) {
                // If search field is cleared, fetch all trending movies
                fetchTrendingMovies()
            } else {
                searchMovies(charSequence.toString()) // Search for movies as the user types
            }
        }

        // Fetch trending movies initially
        fetchTrendingMovies()

        return view
    }

    private fun fetchTrendingMovies() {
        val apiKey = "1002b252d6774f7c3dbd855cf7d3e459"
        lifecycleScope.launch {
            try {
                val response = movieApi.getTrendingMovies(apiKey) // Fetch trending movies
                if (response.isSuccessful) {
                    val movieList = response.body()?.results ?: emptyList()
                    moviesList.clear()
                    moviesList.addAll(movieList)
                    movieAdapter.notifyDataSetChanged()
                    progressBarrer.visibility = View.GONE // Hide progress bar after data is loaded
                } else {
                    showError("Error: Failed to fetch trending movies")
                }
            } catch (e: Exception) {
                showError("Exception: ${e.message}")
            }
        }
    }

    private fun searchMovies(query: String) {
        val apiKey = "1002b252d6774f7c3dbd855cf7d3e459"
        lifecycleScope.launch {
            try {
                val response = movieApi.searchMovies(apiKey, query) // Use search API endpoint
                if (response.isSuccessful) {
                    val movieList = response.body()?.results ?: emptyList()
                    moviesList.clear()
                    moviesList.addAll(movieList)
                    movieAdapter.notifyDataSetChanged()
                    progressBarrer.visibility = View.GONE // Hide progress bar after data is loaded
                } else {
                    showError("Error: Failed to fetch search results")
                }
            } catch (e: Exception) {
                showError("Exception: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
