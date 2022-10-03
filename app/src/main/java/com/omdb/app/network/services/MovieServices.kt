package com.omdb.app.network.services

import com.omdb.app.domain.model.MovieDetailResponse
import com.omdb.app.domain.model.MovieSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieServices {

    @GET("?")
    suspend fun getAllMovie(
        @Query("s") search: String,
        @Query("apikey") apikey: String,
        @Query("page") page: Int,
    ) : MovieSearchResponse

    @GET("?")
    suspend fun getMovieDetail(
        @Query("i") id: String,
        @Query("apikey") apikey: String,
    ) : MovieDetailResponse

}