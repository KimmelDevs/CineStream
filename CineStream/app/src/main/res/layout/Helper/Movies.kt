package com.example.cinestream.Helper


data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val poster_path: String?,
    val release_date: String?,
    val vote_average: Double?,
    val vote_count: Int?,
    val popularity: Double?,
    val genre_ids: List<Int>?,
    val backdrop_path: String?,
    val original_title: String?,
    val original_language: String?,
    val adult: Boolean?,
    val video: Boolean?
)


data class ProductionCompany(
    val id: Int,
    val name: String,
    val logo_path: String?,
    val origin_country: String
)

data class ProductionCountry(
    val iso_3166_1: String,
    val name: String
)

data class SpokenLanguage(
    val iso_639_1: String,
    val name: String
)

data class MovieResponses(
    val page: Int,
    val results: List<Movie>,
    val total_results: Int,
    val total_pages: Int
)
