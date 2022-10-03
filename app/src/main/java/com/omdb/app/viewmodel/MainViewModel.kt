package com.omdb.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.omdb.app.domain.model.MovieSearchItem
import com.omdb.app.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(val
    movieRepository:  MovieRepository,
): ViewModel() {
    var searchQuery:String="fast"
    var currentMovieList: Flow<PagingData<MovieSearchItem>>?=null
    //= movieRepository.getAllMovie(searchQuery).cachedIn(viewModelScope)



    fun searchMovie(search: String): Flow<PagingData<MovieSearchItem>> {
        val lastResult = currentMovieList
        if (searchQuery == search && lastResult != null) {
            return lastResult
        }
        searchQuery = search
        val newResult = movieRepository.getAllMovie(search)
            .cachedIn(viewModelScope)
        currentMovieList = newResult
        return newResult
    }



}