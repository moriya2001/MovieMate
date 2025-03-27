package net.moviemate.app.retrofit

import net.moviemate.app.m.MovieResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {

    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") query: String
    ): Response<MovieResponse>

}