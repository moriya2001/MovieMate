package net.moviemate.app.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.moviemate.app.m.MovieEntity

@Dao
interface OmDbDao {
    @Query("SELECT * FROM omdb_movies")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM omdb_movies")
    suspend fun clearMovies()
}
