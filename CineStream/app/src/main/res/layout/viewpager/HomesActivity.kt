package com.example.cinestream.viewpager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.cinestream.R
import com.example.cinestream.fragments.HomeFragment
import com.example.cinestream.fragments.FavoriteFragment
import com.example.cinestream.fragments.ProfileFragment
import com.google.android.material.tabs.TabLayout

class HomesActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homes)

        tabLayout = findViewById(R.id.tabLayout)

        // Add tabs to TabLayout with custom views
        val homeTab = layoutInflater.inflate(R.layout.tab_home, null)
        val favoritesTab = layoutInflater.inflate(R.layout.tab_favorites, null)
        val profileTab = layoutInflater.inflate(R.layout.tab_profile, null)

        // Set custom views for each tab
        tabLayout.addTab(tabLayout.newTab().setCustomView(homeTab))
        tabLayout.addTab(tabLayout.newTab().setCustomView(favoritesTab))
        tabLayout.addTab(tabLayout.newTab().setCustomView(profileTab))

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment()) // Show HomeFragment by default
        }

        // Handle tab selection and fragment loading
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.position) {
                        0 -> loadFragment(HomeFragment()) // Home tab
                        1 -> loadFragment(FavoriteFragment()) // Favorites tab
                        2 -> loadFragment(ProfileFragment()) // Profile tab
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadFragment(fragment: Fragment) {
        // Replace the current fragment with the selected one
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment) // fragmentContainer is the ID of the FrameLayout container in the layout
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}
