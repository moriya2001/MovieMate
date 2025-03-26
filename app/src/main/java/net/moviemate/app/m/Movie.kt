package net.moviemate.app.m

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "movies")
@Parcelize
data class Movie(
    @PrimaryKey val id:String,
    val title:String,
    val review:String,
    val link:String,
    val image:String,
    val userId:String,
    val username:String,
    val userImage:String,
    val timeStamp:Long
): Parcelable {
    constructor():this("","","","","","","","",0)
}
