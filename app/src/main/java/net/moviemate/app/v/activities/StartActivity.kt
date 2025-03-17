package net.moviemate.app.v.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import net.moviemate.app.R
import net.moviemate.app.vm.AuthViewModel

class StartActivity : AppCompatActivity() {

    private lateinit var authViewModel:AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.userLiveData.observe(this@StartActivity) { user ->
            user?.let {
                startActivity(Intent(this@StartActivity,MainActivity::class.java)).apply {
                    finish()
                }
            } ?: run {
                startActivity(Intent(this@StartActivity,LoginSignUpActivity::class.java)).apply {
                    finish()
                }
            }
        }
    }
}