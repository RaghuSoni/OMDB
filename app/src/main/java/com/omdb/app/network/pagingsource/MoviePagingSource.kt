package com.omdb.app.network.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.omdb.app.domain.model.MovieSearchItem
import com.omdb.app.domain.model.MovieSearchResponse
import com.omdb.app.utils.Const

class MoviePagingSource(
    private val response: suspend (Int) -> MovieSearchResponse,
) : PagingSource<Int, MovieSearchItem>() {

    override fun getRefreshKey(state: PagingState<Int, MovieSearchItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieSearchItem> {
        return try {

            val nextPage = params.key ?: 1
            val moviesList = response.invoke(nextPage)
            val newCount = moviesList.searchList.size
            val total = moviesList.totalResults
            val itemsBefore = nextPage * Const.PAGE_SIZE
            val itemsAfter = (total?.toInt() ?: 1) - (itemsBefore + newCount)

            val prevKey = if (nextPage == 0) null else nextPage - 1
            val nextKey = if (itemsAfter == 0) null else nextPage + 1

            if(moviesList.response.equals("False")){
                throw Exception("No Data Found")
            }else{
                LoadResult.Page(
                    data = moviesList.searchList,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }
}