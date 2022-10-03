package com.omdb.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omdb.app.domain.model.MovieDetailResponse
import com.omdb.app.domain.repository.MovieRepository
import com.omdb.app.utils.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailModel @Inject constructor(
    private val movieRepository: MovieRepository,
): ViewModel() {

    private val _movieState = MutableStateFlow<Response<MovieDetailResponse>>(Response.Success(null))
    val movieState: StateFlow<Response<MovieDetailResponse>> = _movieState


    fun getDetailMovie(id: String){
        viewModelScope.launch {
            movieRepository.getDetailMovie(id).collect { response ->
                _movieState.value = response

            }
        }
    }
}