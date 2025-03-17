package net.moviemate.app.utils

import net.moviemate.app.m.User


object UserSession {
    var currentUser: User? = null

    fun setUser(user: User) {
        currentUser = user
    }

    fun getUser(): User? {
        return currentUser
    }

    fun clearUser() {
        currentUser = null
    }
}