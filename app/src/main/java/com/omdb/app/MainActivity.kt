package com.omdb.app


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.omdb.app.adapter.MovieLoadStateAdapter
import com.omdb.app.adapter.MoviePagerAdapter
import com.omdb.app.databinding.ActivityMainBinding
import com.omdb.app.listeners.MovieClickListener
import com.omdb.app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    var searchJob: Job? = null
    private val viewModel by viewModels<MainViewModel>()
    private var moviePagerAdapter:MoviePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.executePendingBindings()

        setToolbar()
        initRecyclerView()
        initSearch()

        searchJob = lifecycleScope.launchWhenCreated {
            viewModel.searchMovie("fast").collectLatest {
                moviePagerAdapter?.submitData(it)
            }
        }

    }

    private fun setToolbar() {
        binding.toolbar.setTitleTextColor(Color.WHITE);
        binding.toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.browse)
        supportActionBar?.subtitle = getString(R.string.movies)
    }

    private fun initRecyclerView() {
        moviePagerAdapter= MoviePagerAdapter(MovieClickListener {
            val intent=Intent(this@MainActivity,MovieDetailActivity::class.java)
            intent.putExtra(getString(R.string.movieid),it.imdbID)
            startActivity(intent)
        })
        val gridLayoutManager=GridLayoutManager(this,3)
       /* gridLayoutManager.setSpanSizeLookup(object : GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                if(moviePagerAdapter.snapshot().items.get(position) is MovieSearchItem){
                    return 1
                }else{
                    return 3
                }
            }
        })*/
        binding.recyclerview.apply {
            layoutManager=gridLayoutManager
            adapter = moviePagerAdapter?.withLoadStateFooter(
                footer = MovieLoadStateAdapter { moviePagerAdapter?.retry() }
            )
        }

        moviePagerAdapter?.addLoadStateListener { loadState ->

            if (loadState.refresh is LoadState.Loading) {
                binding.loadStateRetry.visibility = View.GONE
                binding.loadStateErrorMessage.visibility = View.GONE
                binding.loadStateProgress.visibility = View.VISIBLE
            } else {
                binding.loadStateProgress.visibility = View.GONE

                val errorState = when {
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.refresh is LoadState.Error -> {
                        binding.loadStateRetry.visibility = View.VISIBLE
                        loadState.refresh as LoadState.Error
                    }
                    else -> null
                }
                errorState?.let {
                    Toast.makeText(this, it.error.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun initSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                updateSearchResult(query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

    }

    private fun updateSearchResult(searchQuery: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchMovie(searchQuery).collect { moviePagerAdapter?.submitData(it) }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.demo, menu)
        return true
    }
}