package net.moviemate.app.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.moviemate.app.room.MovieDao
import net.moviemate.app.vm.MoviesViewModel

class MoviesViewModelFactory(private val movieDao: MovieDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MoviesViewModel(movieDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
