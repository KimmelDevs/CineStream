package com.example.cinestream.Helper

import android.telecom.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApi {
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String
    ): Response<MovieResponses>

    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String
    ): Response<MovieResponses>

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: Int
    ): Response<MovieResponses>
    @GET("discover/movie")
    suspend fun getMoviesByCountryAndGenre(
        @Query("api_key") apiKey: String,
        @Query("with_origin_country") countryCode: String,
        @Query("with_genres") genreId: Int
    ): Response<MovieResponses>
    // New endpoint to get movie details by movie ID
    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<Movie>  // Movie is the data class that contains detailed movie info

    @GET("discover/movie")
    fun getAllMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<MovieResponses>
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String,
        @Query("query") query: String
    ): Response<MovieResponses>
}
