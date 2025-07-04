package com.example.cinestream.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cinestream.R
import com.example.cinestream.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the EditText fields and Sign In button
        val usrnameinput: EditText = findViewById(R.id.usrnameinput)
        val usrinputEmail: EditText = findViewById(R.id.usrinputEmail)
        val usrinputPassword: EditText = findViewById(R.id.usrinputPassword)
        val confirmPassword: EditText = findViewById(R.id.ConfirmPassword)
        val signinButton: Button = findViewById(R.id.btn_sign_in)

        // Set up the button click listener
        signinButton.setOnClickListener {
            val username = usrnameinput.text.toString().trim()
            val email = usrinputEmail.text.toString().trim()
            val password = usrinputPassword.text.toString().trim()
            val confirmpass = confirmPassword.text.toString().trim()

            // Enhanced Validation Logic
            when {
                username.isEmpty() -> {
                    Toast.makeText(this, "Username cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                email.isEmpty() -> {
                    Toast.makeText(this, "Email cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show()
                }
                password.length < 6 -> {
                    Toast.makeText(this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show()
                }
                confirmpass.isEmpty() -> {
                    Toast.makeText(this, "Please confirm your password!", Toast.LENGTH_SHORT).show()
                }
                password != confirmpass -> {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Sign up the user with Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Get the user ID
                                val userId = auth.currentUser?.uid ?: ""

                                // Create a User object
                                val user = User(
                                    id = userId,
                                    username = username,
                                    email = email
                                )

                                // Save the user in Firestore
                                firestore.collection("users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show()
                                        // Navigate to the LoginActivity or HomeActivity
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to save user: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Registration failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
        }

        // Find the Already have Account button and set up the click listener
        val alreadyhaveAccountButton: TextView = findViewById(R.id.AlreadyhaveAcc)
        alreadyhaveAccountButton.setOnClickListener {
            // Start the LoginActivity when the button is clicked
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Added: Auto-clear errors on text change for all input fields
        clearErrorsOnTextChange(usrnameinput)
        clearErrorsOnTextChange(usrinputEmail)
        clearErrorsOnTextChange(usrinputPassword)
        clearErrorsOnTextChange(confirmPassword)
    }

    // Function to auto-clear error messages when user starts typing
    private fun clearErrorsOnTextChange(editText: EditText) {
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText.error = null
            }
        }
    }
}
