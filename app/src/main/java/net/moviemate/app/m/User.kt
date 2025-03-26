package net.moviemate.app.m

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId:String,
    val username:String,
    val email:String,
    val image:String
): Parcelable {
    constructor():this("","","","")
}
