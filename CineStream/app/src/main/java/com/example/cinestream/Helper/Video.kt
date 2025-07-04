package com.example.cinestream.Helper


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val size: Int,
    val type: String
)

suspend fun fetchMovieTrailer(movieId: Int, apiKey: String): String? {
    val url = "https://api.themoviedb.org/3/movie/$movieId/videos?api_key=$apiKey"
    return withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().readText()
                val jsonResponse = JSONObject(response)
                val results = jsonResponse.getJSONArray("results")

                for (i in 0 until results.length()) {
                    val video = results.getJSONObject(i)
                    if (video.getString("type") == "Trailer" && video.getString("site") == "YouTube") {
                        val key = video.getString("key")
                        return@withContext "https://www.youtube.com/watch?v=$key"
                    } else if (video.getString("type") == "Trailer" && video.getString("site") == "Vimeo") {
                        val key = video.getString("key")
                        return@withContext "https://vimeo.com/$key"
                    }

                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
