package com.omdb.app

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.omdb.app.databinding.ActivityMovieDetailBinding
import com.omdb.app.utils.Response
import com.omdb.app.viewmodel.MovieDetailModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMovieDetailBinding
    private val viewModel by viewModels<MovieDetailModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.executePendingBindings()

        intent.getStringExtra(getString(R.string.movieid))?.let {
            viewModel.getDetailMovie(it)
        }


        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED){

                viewModel.movieState.collect{state->
                    when(state){
                        is Response.Loading ->{

                        }
                        is Response.Success ->{
                            Glide.with(this@MovieDetailActivity)
                                .load(state.data?.poster)
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .into(binding.imageView)
                            binding.title.text=state.data?.title
                        }
                        is Response.Failure->{

                        }
                    }

                }

            }
        }

    }


}