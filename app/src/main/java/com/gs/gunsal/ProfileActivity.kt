package com.gs.gunsal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.mikephil.charting.utils.Utils.init
import com.gs.gunsal.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    lateinit var userId : String
    lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userId = intent.getStringExtra("USER_ID").toString()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.submitBtn.setOnClickListener {
            if(binding.userHeight.text.isNotBlank()&&binding.userWeight.text.isNotBlank()&&binding.nickName.text.isNotBlank()){
                val nickName = binding.nickName.text.toString()
                val height = binding.userHeight.text.toString().toDouble()
                val weight = binding.userWeight.text.toString().toDouble()
                val age = binding.userAge.text.toString().toInt()
                FirebaseRepository.updateBodyData(userId, height, weight)
                FirebaseRepository.updateUserNickName(userId, nickName)
                FirebaseRepository.updateUserAge(userId, age)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(applicationContext, "항목을 다 채워주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}