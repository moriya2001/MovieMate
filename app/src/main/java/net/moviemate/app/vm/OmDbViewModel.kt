package net.moviemate.app.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import net.moviemate.app.m.MovieEntity
import net.moviemate.app.retrofit.MovieApiService
import net.moviemate.app.room.OmDbDao

class OmDbViewModel(private val omDbDao: OmDbDao, private val movieApiService: MovieApiService) : ViewModel() {

    private val _movies = MutableLiveData<List<MovieEntity>>()
    val movies: LiveData<List<MovieEntity>> get() = _movies

    init {
        loadMoviesFromDatabase()
    }

    private fun loadMoviesFromDatabase() {
        viewModelScope.launch {
            omDbDao.getAllMovies().collect { moviesList ->
                _movies.postValue(moviesList)
            }
        }
    }

    fun fetchMovies(apiKey: String, query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                omDbDao.getAllMovies().collect { moviesList ->
                    _movies.postValue(moviesList)
                }
            } else {
                try {
                    val response = movieApiService.searchMovies(apiKey, query)
                    if (response.isSuccessful) {
                        response.body()?.Search?.let { moviesList ->
                            val movieEntities = moviesList.map {
                                MovieEntity(it.imdbID, it.Title, it.Year, it.Type, it.Poster)
                            }
                            omDbDao.clearMovies()
                            omDbDao.insertMovies(movieEntities)
                            _movies.postValue(movieEntities)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}
