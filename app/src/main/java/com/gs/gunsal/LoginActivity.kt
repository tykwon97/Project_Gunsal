package com.gs.gunsal

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.mikephil.charting.utils.Utils.init
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.FitnessStatusCodes
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_CUMULATIVE
import com.google.android.gms.fitness.data.DataType.TYPE_STEP_COUNT_DELTA
import com.gs.gunsal.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    var fitnessOptions: FitnessOptions = FitnessOptions.builder()
        .addDataType(TYPE_STEP_COUNT_CUMULATIVE)
        .addDataType(TYPE_STEP_COUNT_DELTA)
        .build()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this),fitnessOptions)) {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
    private fun init() {
        binding.setProfile.setOnClickListener {
            GoogleSignIn.requestPermissions(this, 100, GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)
        }
    }
}