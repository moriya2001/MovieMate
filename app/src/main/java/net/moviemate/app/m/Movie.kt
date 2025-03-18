package net.moviemate.app.m

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movies")
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
):Serializable{
    constructor():this("","","","","","","","",0)
}
