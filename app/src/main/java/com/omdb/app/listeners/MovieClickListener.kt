package com.omdb.app.listeners

import com.omdb.app.domain.model.MovieSearchItem

data class MovieClickListener(val clickListener: (wallpaper: MovieSearchItem) -> Unit)
