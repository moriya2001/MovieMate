package net.moviemate.app.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.moviemate.app.m.Movie
import net.moviemate.app.room.MovieDao

class MoviesViewModel(private val movieDao: MovieDao) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _moviesLiveData = MutableLiveData<List<Movie>>()
    val moviesLiveData: LiveData<List<Movie>> get() = _moviesLiveData

    private val _userMoviesLiveData = MutableLiveData<List<Movie>>()
    val userMoviesLiveData: LiveData<List<Movie>> get() = _userMoviesLiveData


    fun insertMovie(movie: Movie, imageUri: Uri?) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            movieDao.insertMovie(movie)
            imageUri?.let {
                uploadMovieImage(movie.id, it) { imageUrl ->
                    viewModelScope.launch(Dispatchers.IO) {
                        val updatedMovie = movie.copy(image = imageUrl)
                        movieDao.updateMovie(updatedMovie)
                        saveMovieToFirestore(updatedMovie)
                        _isLoading.postValue(false)
                        _message.postValue("Movie added successfully!")
                    }
                }
            } ?: run {
                saveMovieToFirestore(movie)
                _isLoading.postValue(false)
                _message.postValue("Movie added successfully!")
            }
        }
    }


    fun updateMovie(movie: Movie, imageUri: Uri?) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            movieDao.updateMovie(movie)
            imageUri?.let {
                uploadMovieImage(movie.id, it) { imageUrl ->
                    viewModelScope.launch(Dispatchers.IO) {
                        val updatedMovie = movie.copy(image = imageUrl)
                        movieDao.updateMovie(updatedMovie)
                        saveMovieToFirestore(updatedMovie)
                        _isLoading.postValue(false)
                        _message.postValue("Movie updated successfully!")
                    }
                }
            } ?: run {
                saveMovieToFirestore(movie)
                _isLoading.postValue(false)
                _message.postValue("Movie updated successfully!")
            }
        }
    }


    fun deleteMovie(movie: Movie) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            movieDao.deleteMovie(movie)
            firestore.collection("movies").document(movie.id).delete()
            _isLoading.postValue(false)
            _message.postValue("Movie deleted successfully!")
        }
    }


    fun getAllMovies() {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val moviesList = mutableListOf<Movie>()
            firestore.collection("movies").get().addOnSuccessListener { result ->
                for (document in result) {
                    document.toObject(Movie::class.java)?.let { moviesList.add(it) }
                }
                viewModelScope.launch {
                    movieDao.clearMovies()
                    moviesList.forEach { movieDao.insertMovie(it) }
                    _moviesLiveData.postValue(movieDao.getAllMovies())
                    _isLoading.postValue(false)
                }

            }
        }
    }

     fun getAllMoviesByUserId(userId:String) {
        _isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch {
                _userMoviesLiveData.postValue(movieDao.getMoviesByUserId(userId))
                _isLoading.postValue(false)
            }
        }
    }


    private fun saveMovieToFirestore(movie: Movie) {
        firestore.collection("movies").document(movie.id).set(movie)
    }


    private fun uploadMovieImage(movieId: String, imageUri: Uri, callback: (String) -> Unit) {
        val ref = storage.child("movies/$movieId.jpg")
        ref.putFile(imageUri).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener { uri -> callback(uri.toString()) }
        }
    }
}
