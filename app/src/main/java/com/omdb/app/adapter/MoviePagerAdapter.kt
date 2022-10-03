package com.omdb.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.omdb.app.R
import com.omdb.app.databinding.MovieSearchItemBinding
import com.omdb.app.domain.model.MovieSearchItem
import com.omdb.app.listeners.MovieClickListener


class MoviePagerAdapter(private val clickListener: MovieClickListener): PagingDataAdapter<MovieSearchItem, MoviePagerAdapter.MovieViewHolder>(MovieComparator) {

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)!!
        holder.bind(movie,clickListener.clickListener)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding:MovieSearchItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.movie_search_item,parent, false)
        return MovieViewHolder(binding)
    }

    class MovieViewHolder(val binding : MovieSearchItemBinding): RecyclerView.ViewHolder(binding.root) {
         fun bind(movieItem:MovieSearchItem,clickListener: (MovieSearchItem) -> Unit){
            binding.root.setOnClickListener{
                clickListener(movieItem)
            }
            binding.model=movieItem
            binding.executePendingBindings()
        }
    }

    object MovieComparator: DiffUtil.ItemCallback<MovieSearchItem>() {
        override fun areItemsTheSame(oldItem: MovieSearchItem, newItem: MovieSearchItem): Boolean {
            // Id is unique.
            return oldItem.imdbID == newItem.imdbID
        }

        override fun areContentsTheSame(oldItem: MovieSearchItem, newItem: MovieSearchItem): Boolean {
            return oldItem == newItem
        }
    }
}