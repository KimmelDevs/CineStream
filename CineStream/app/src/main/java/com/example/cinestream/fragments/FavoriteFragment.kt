//FavoriteFragment
package com.example.cinestream.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinestream.Adaptor.FavoriteMoviesAdapter
import com.example.cinestream.Domain.FavoriteViewModel
import com.example.cinestream.Domain.FavoriteViewModelFactory
import com.example.cinestream.Helper.RetrofittInstance
import com.example.cinestream.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteMoviesAdapter

    private lateinit var viewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerView = view.findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val apiService = RetrofittInstance.api

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            FavoriteViewModelFactory(firestore, auth, apiService)
        )[FavoriteViewModel::class.java]

        // Observe LiveData
        viewModel.favoriteMovies.observe(viewLifecycleOwner) { movies ->
            adapter = FavoriteMoviesAdapter(movies.toMutableList())
            recyclerView.adapter = adapter
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        // Load favorites
        viewModel.loadFavorites()

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }
}
