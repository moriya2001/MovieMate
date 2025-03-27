package net.moviemate.app.m

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "omdb_movies")
@Parcelize
data class MovieEntity(
    @PrimaryKey val imdbID: String,
    val Title: String,
    val Year: String,
    val Type: String,
    val Poster: String
):Parcelable

