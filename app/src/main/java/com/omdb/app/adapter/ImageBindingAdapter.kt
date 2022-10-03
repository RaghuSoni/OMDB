package com.omdb.app.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.omdb.app.R

@BindingAdapter("imageUrl")
fun ImageView.loadImage(url: String) {
    Glide.with(context)
        .load(url)
        .placeholder(R.mipmap.ic_launcher)
        .error(R.mipmap.ic_launcher)
        .into(this)
}
