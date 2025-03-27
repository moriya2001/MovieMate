package net.moviemate.app.vmf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.moviemate.app.retrofit.MovieApiService
import net.moviemate.app.room.MovieDao
import net.moviemate.app.room.OmDbDao
import net.moviemate.app.vm.MoviesViewModel
import net.moviemate.app.vm.OmDbViewModel

class OmDbMoviesViewModelFactory(
    private val omDbDao: OmDbDao,
    private val movieApiService: MovieApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OmDbViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OmDbViewModel(omDbDao, movieApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
