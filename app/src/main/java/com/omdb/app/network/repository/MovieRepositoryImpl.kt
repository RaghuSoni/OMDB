package com.omdb.app.network.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.omdb.app.domain.model.MovieDetailResponse
import com.omdb.app.domain.model.MovieSearchItem
import com.omdb.app.domain.repository.MovieRepository
import com.omdb.app.network.pagingsource.MoviePagingSource
import com.omdb.app.network.services.MovieServices
import com.omdb.app.utils.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val movieService: MovieServices,
    private val pageSize: Int,
    private val apiKey: String,
): MovieRepository {

    override fun getAllMovie(search: String): Flow<PagingData<MovieSearchItem>> = Pager(
        config = PagingConfig(
            pageSize = pageSize,
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            MoviePagingSource(
                response = { pageNext ->
                    movieService.getAllMovie(
                        search = search,
                        apikey = apiKey,
                        page = pageNext,
                    )
                }
            )
        }
    ).flow


    override fun getDetailMovie(id: String): Flow<Response<MovieDetailResponse>> = flow{
        try {
            emit(Response.Loading)
            val responseApi = movieService.getMovieDetail(
                id = id,
                apikey = apiKey
            )
            if(responseApi.response.equals("False")){
               throw Exception()
            }else{
                emit(Response.Success(responseApi))
            }

        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }.flowOn(Dispatchers.IO)
}