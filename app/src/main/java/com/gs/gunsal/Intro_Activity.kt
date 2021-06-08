package com.gs.gunsal

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnCompleteListener
import com.gs.gunsal.databinding.ActivityIntroBinding

class Intro_Activity : AppCompatActivity() {
    lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initIntro()
    }


    private fun initIntro() {
            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this))) {
                Handler().postDelayed({
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }, 3000L)
            } else {

                Handler().postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 3000L)
            }
    }
}