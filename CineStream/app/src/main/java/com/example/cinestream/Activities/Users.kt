package com.example.cinestream.Models

// Data class to represent a user in Firestore
data class User(
    val id: String = "",
    val username: String = "",
    val email: String = "",
    val favorites: List<Int> = emptyList() // List of favorite movie IDs
)
