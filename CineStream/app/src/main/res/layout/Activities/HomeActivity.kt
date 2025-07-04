package com.example.cinestream.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinestream.Adaptor.ViewAdapter
import com.example.cinestream.Helper.RetrofittInstance
import com.example.cinestream.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val favoritesLayout = findViewById<LinearLayout>(R.id.Favorites)

        // Set an OnClickListener
        favoritesLayout.setOnClickListener {
            // Create an Intent to start FavoritesActivity
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent) // Start the activity
        }
        initView()
        fetchMovies()
    }

    private fun initView() {
        // Initialize RecyclerViews
        val recyclerViewUpcomingMovies: RecyclerView = findViewById(R.id.UpcomingMovies)
        val recyclerViewTrendingMovies: RecyclerView = findViewById(R.id.TrendingMovies)
        val recyclerViewKoreanMovies: RecyclerView = findViewById(R.id.KdramaMovies)
        val recyclerViewDramaMovies: RecyclerView = findViewById(R.id.DramaMovies)
        val recyclerViewComedyMovies: RecyclerView = findViewById(R.id.ComedyMovies)
        val recyclerViewThrillerMovies: RecyclerView = findViewById(R.id.ThrillerMovies)



        recyclerViewUpcomingMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTrendingMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewKoreanMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDramaMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewComedyMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewThrillerMovies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Initialize ProgressBars
        progressBarUpcoming = findViewById(R.id.progressBar)
        progressBarTrending = findViewById(R.id.progressBar4)
        progressBarKDRAMA = findViewById(R.id.progressBar5)
        progressBarDrama = findViewById(R.id.progressBar6)
        progressBarComedy = findViewById(R.id.progressBar7)
        progressBarThriller = findViewById(R.id.progressBar8)

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

    }

    private fun fetchMovies() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val upcomingResponse = RetrofittInstance.api.getUpcomingMovies("1002b252d6774f7c3dbd855cf7d3e459")
                val trendingResponse = RetrofittInstance.api.getTrendingMovies("1002b252d6774f7c3dbd855cf7d3e459")
                val kDramaResponse = RetrofittInstance.api.getMoviesByCountryAndGenre(
                    apiKey = "1002b252d6774f7c3dbd855cf7d3e459",
                    countryCode = "KR",
                    genreId = 18 // Drama genre ID (if genre filtering is used)
                )

                val dramaResponse = RetrofittInstance.api.getMoviesByGenre("1002b252d6774f7c3dbd855cf7d3e459",18 ) // Comedy Genre ID: 35

                val comedyResponse = RetrofittInstance.api.getMoviesByGenre("1002b252d6774f7c3dbd855cf7d3e459",35 ) // Comedy Genre ID: 35
                val thrillerResponse = RetrofittInstance.api.getMoviesByGenre("1002b252d6774f7c3dbd855cf7d3e459",53) // Thriller Genre ID: 53

                if (upcomingResponse.isSuccessful && trendingResponse.isSuccessful) {
                    val upcomingMovies = upcomingResponse.body()?.results ?: emptyList()
                    val trendingMovies = trendingResponse.body()?.results ?: emptyList()
                    val kDramaMovies = kDramaResponse.body()?.results ?: emptyList()

                    val dramaMovies = dramaResponse.body()?.results ?: emptyList()
                    val ComedyMovies = comedyResponse.body()?.results ?: emptyList()
                    val ThrillerMovies = thrillerResponse.body()?.results ?: emptyList()



                    withContext(Dispatchers.Main) {
                        adapterUpcomingMovies.updateMovies(upcomingMovies)
                        adapterTrendingMovies.updateMovies(trendingMovies)
                        adapterKoreanMovies.updateMovies(kDramaMovies)
                        adapterComedyMovies.updateMovies(ComedyMovies)
                        adapterThrillerMovies.updateMovies(ThrillerMovies)
                        adapterDramaMovies.updateMovies(dramaMovies)

                        progressBarKDRAMA.visibility = View.GONE
                        progressBarUpcoming.visibility = View.GONE
                        progressBarTrending.visibility = View.GONE
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
