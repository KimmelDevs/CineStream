package com.example.cinestream.Helper


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofittInstance {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val api: MovieApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovieApi::class.java)
    }
}
