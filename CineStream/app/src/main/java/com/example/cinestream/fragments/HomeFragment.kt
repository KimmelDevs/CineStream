//HomeFragment
package com.example.cinestream.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinestream.Adaptor.ViewAdapter
import com.example.cinestream.Helper.RetrofittInstance
import com.example.cinestream.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var adapterUpcomingMovies: ViewAdapter
    private lateinit var adapterTrendingMovies: ViewAdapter
    private lateinit var adapterKoreanMovies: ViewAdapter
    private lateinit var adapterDramaMovies: ViewAdapter
    private lateinit var adapterComedyMovies: ViewAdapter
    private lateinit var adapterThrillerMovies: ViewAdapter
    private lateinit var progressBarUpcoming: ProgressBar
    private lateinit var progressBarTrending: ProgressBar
    private lateinit var progressBarKDRAMA: ProgressBar
    private lateinit var progressBarDrama: ProgressBar
    private lateinit var progressBarComedy: ProgressBar
    private lateinit var progressBarThriller: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        val recyclerViewUpcomingMovies: RecyclerView = view.findViewById(R.id.UpcomingMovies)
        val recyclerViewTrendingMovies: RecyclerView = view.findViewById(R.id.TrendingMovies)
        val recyclerViewKoreanMovies: RecyclerView = view.findViewById(R.id.KdramaMovies)
        val recyclerViewDramaMovies: RecyclerView = view.findViewById(R.id.DramaMovies)
        val recyclerViewComedyMovies: RecyclerView = view.findViewById(R.id.ComedyMovies)
        val recyclerViewThrillerMovies: RecyclerView = view.findViewById(R.id.ThrillerMovies)
        val editText = view.findViewById<TextView>(R.id.editTextText)

        // Set the click listener
        editText.setOnClickListener {
            val fragmentManager = parentFragmentManager
            val transaction = fragmentManager.beginTransaction()

            // Replace the current fragment with the SearchFragment
            transaction.replace(R.id.fragmentContainer, SearchFragment())
            transaction.addToBackStack(null) // Adds this transaction to the back stack
            transaction.commit()
        }
        // Set up RecyclerView layout managers
        recyclerViewUpcomingMovies.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTrendingMovies.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewKoreanMovies.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDramaMovies.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewComedyMovies.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewThrillerMovies.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Initialize ProgressBars
        progressBarUpcoming = view.findViewById(R.id.progressBar)
        progressBarTrending = view.findViewById(R.id.progressBar4)
        progressBarKDRAMA = view.findViewById(R.id.progressBar5)
        progressBarDrama = view.findViewById(R.id.progressBar6)
        progressBarComedy = view.findViewById(R.id.progressBar7)
        progressBarThriller = view.findViewById(R.id.progressBar8)

        // Initialize Adapters
        adapterUpcomingMovies = ViewAdapter(mutableListOf())
        adapterTrendingMovies = ViewAdapter(mutableListOf())
        adapterKoreanMovies = ViewAdapter(mutableListOf())
        adapterDramaMovies = ViewAdapter(mutableListOf())
        adapterComedyMovies = ViewAdapter(mutableListOf())
        adapterThrillerMovies = ViewAdapter(mutableListOf())

        // Set adapters to RecyclerViews
        recyclerViewUpcomingMovies.adapter = adapterUpcomingMovies
        recyclerViewTrendingMovies.adapter = adapterTrendingMovies
        recyclerViewKoreanMovies.adapter = adapterKoreanMovies
        recyclerViewDramaMovies.adapter = adapterDramaMovies
        recyclerViewComedyMovies.adapter = adapterComedyMovies
        recyclerViewThrillerMovies.adapter = adapterThrillerMovies


        // Fetch movie data
        fetchMovies()

        return view
    }

    private fun fetchMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val upcomingResponse = RetrofittInstance.api.getUpcomingMovies("1002b252d6774f7c3dbd855cf7d3e459")
                val trendingResponse = RetrofittInstance.api.getTrendingMovies("1002b252d6774f7c3dbd855cf7d3e459")
                val kDramaResponse = RetrofittInstance.api.getMoviesByCountryAndGenre(
                    apiKey = "1002b252d6774f7c3dbd855cf7d3e459",
                    countryCode = "KR",
                    genreId = 18
                )
                val dramaResponse = RetrofittInstance.api.getMoviesByGenre("1002b252d6774f7c3dbd855cf7d3e459", 18)
                val comedyResponse = RetrofittInstance.api.getMoviesByGenre("1002b252d6774f7c3dbd855cf7d3e459", 35)
                val thrillerResponse = RetrofittInstance.api.getMoviesByGenre("1002b252d6774f7c3dbd855cf7d3e459", 53)

                if (upcomingResponse.isSuccessful && trendingResponse.isSuccessful) {
                    val upcomingMovies = upcomingResponse.body()?.results ?: emptyList()
                    val trendingMovies = trendingResponse.body()?.results ?: emptyList()
                    val kDramaMovies = kDramaResponse.body()?.results ?: emptyList()
                    val dramaMovies = dramaResponse.body()?.results ?: emptyList()
                    val comedyMovies = comedyResponse.body()?.results ?: emptyList()
                    val thrillerMovies = thrillerResponse.body()?.results ?: emptyList()

                    withContext(Dispatchers.Main) {
                        adapterUpcomingMovies.updateMovies(upcomingMovies)
                        adapterTrendingMovies.updateMovies(trendingMovies)
                        adapterKoreanMovies.updateMovies(kDramaMovies)
                        adapterDramaMovies.updateMovies(dramaMovies)
                        adapterComedyMovies.updateMovies(comedyMovies)
                        adapterThrillerMovies.updateMovies(thrillerMovies)

                        progressBarUpcoming.visibility = View.GONE
                        progressBarTrending.visibility = View.GONE
                        progressBarKDRAMA.visibility = View.GONE
                        progressBarDrama.visibility = View.GONE
                        progressBarComedy.visibility = View.GONE
                        progressBarThriller.visibility = View.GONE
                    }
                } else {
                    showError("Error: Failed to fetch movies")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError("Exception: ${e.message}")
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}
