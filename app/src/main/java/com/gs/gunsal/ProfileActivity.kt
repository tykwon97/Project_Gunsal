package com.gs.gunsal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.mikephil.charting.utils.Utils.init
import com.gs.gunsal.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.submitBtn.setOnClickListener {
            if(binding.userHeight.text.isNotBlank()&&binding.userWeight.text.isNotBlank()&&binding.nickName.text.isNotBlank()){

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                Toast.makeText(applicationContext, "항목을 다 채워주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}