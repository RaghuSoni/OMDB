package com.omdb.app.domain.repository

import androidx.paging.PagingData
import com.omdb.app.domain.model.MovieDetailResponse
import com.omdb.app.domain.model.MovieSearchItem
import com.omdb.app.utils.Response
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getAllMovie(search: String): Flow<PagingData<MovieSearchItem>>
    fun getDetailMovie(id: String): Flow<Response<MovieDetailResponse>>
}