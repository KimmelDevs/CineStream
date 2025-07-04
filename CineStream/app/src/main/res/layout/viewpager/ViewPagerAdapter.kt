package com.example.cinestream.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cinestream.fragments.FavoriteFragment
import com.example.cinestream.fragments.HomeFragment
import com.example.cinestream.fragments.ProfileFragment

class ViewPagerAdapter(activity: HomesActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3  // Number of pages (Fragments)

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> FavoriteFragment()
            else -> ProfileFragment()
        }
    }
}
