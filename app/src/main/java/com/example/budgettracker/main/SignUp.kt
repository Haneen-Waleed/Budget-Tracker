package com.example.budgettracker.main

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.budgettracker.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()

        val etName = findViewById<TextInputEditText>(R.id.etName)
        val etEmail = findViewById<TextInputEditText>(R.id.etEmailSignUp)
        val etPassword = findViewById<TextInputEditText>(R.id.etPasswordSignUp)
        val btnSignUp = findViewById<MaterialButton>(R.id.btnSignUp)
        val tvGoToSignIn = findViewById<TextView>(R.id.tvGoToSignIn1)

        btnSignUp.setOnClickListener {

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val uid = auth.currentUser!!.uid

                        val userData = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "balance" to 0.0
                        )

                        db.collection("users")
                            .document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Sign Up Done", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        tvGoToSignIn.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }
    }
}