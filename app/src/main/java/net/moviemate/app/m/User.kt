package net.moviemate.app.m

import java.io.Serializable

data class User(
    val userId:String,
    val username:String,
    val email:String,
    val image:String
):Serializable{
    constructor():this("","","","")
}
