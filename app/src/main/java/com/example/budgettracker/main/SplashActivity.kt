package com.example.budgettracker.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.budgettracker.databinding.SplashScreenBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth;
    private lateinit var binding: SplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

//get started button to navigate to main screen
        binding.getStarted.setOnClickListener {
            val currentUser = auth.currentUser
            if(currentUser != null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
            }

        }
    }
}