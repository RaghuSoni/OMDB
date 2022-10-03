package com.omdb.app


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
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

        setToolbar()

        intent.getStringExtra(getString(R.string.movieid))?.let {
            viewModel.getDetailMovie(it)
        }




        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.movieState.collect { state ->
                    when (state) {
                        is Response.Loading -> {
                            binding.progress.visibility = View.VISIBLE
                            binding.noData.visibility = View.GONE
                            binding.content.visibility = View.GONE
                        }
                        is Response.Success -> {

                            binding.progress.visibility = View.GONE
                            binding.noData.visibility = View.GONE
                            binding.content.visibility = View.VISIBLE

                            Glide.with(this@MovieDetailActivity)
                                .load(state.data?.poster)
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .into(binding.imageView)

                            state.data?.let {
                                binding.title.text = it.title
                                binding.rating.text = it.imdbRating
                                binding.duration.text = it.runtime

                                val list: List<String>? = it.genre?.split(",")?.toList()
                                binding.category.text = list?.get(0) ?: getString(R.string.no_data)
                                binding.action.text = list?.get(1) ?: getString(R.string.no_data)
                                binding.adventure.text = list?.get(2) ?: getString(R.string.no_data)

                                binding.description.text = it.plot
                                binding.directorName.text = it.director
                                binding.writerName.text = it.writer
                                binding.actorName.text = it.actors


                            }

                        }
                        is Response.Failure -> {
                            binding.appBarLayout.setExpanded(false)
                            binding.noData.visibility = View.VISIBLE
                            binding.progress.visibility = View.GONE
                            binding.content.visibility = View.GONE
                        }
                    }

                }

            }
        }

    }

    private fun setToolbar() {
        val toolbar: Toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        collaspingToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun collaspingToolbar() {
        binding.collapsingToolbar.title = " "
        binding.appBarLayout.setExpanded(true)

        binding.appBarLayout.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    binding.collapsingToolbar.title = binding.title.text
                    isShow = true
                } else if (isShow) {
                    binding.collapsingToolbar.title = " "
                    isShow = false
                }
            }
        })
    }


}