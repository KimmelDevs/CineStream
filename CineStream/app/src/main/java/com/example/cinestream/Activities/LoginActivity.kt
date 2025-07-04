package com.example.cinestream.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cinestream.R
import com.example.cinestream.viewpager.HomesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Set up edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the EditText fields and Login button
        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.btn_login)
        val signUpButton: Button = findViewById(R.id.signup_button)

        // Set up the SignUp button click listener
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Set up the Login button click listener
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Validation check
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Authenticate user with Firebase
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                            // Navigate to HomesActivity
                            val intent = Intent(this, HomesActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Handle specific error cases
                            val exception = task.exception
                            when (exception) {
                                is FirebaseAuthInvalidCredentialsException -> {
                                    Toast.makeText(
                                        this,
                                        "Invalid email or password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is FirebaseAuthInvalidUserException -> {
                                    Toast.makeText(
                                        this,
                                        "No account found with this email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is FirebaseAuthWeakPasswordException -> {
                                    Toast.makeText(
                                        this,
                                        "Weak password! Please use a stronger password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is FirebaseAuthEmailException -> {
                                    Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT)
                                        .show()
                                }

                                else -> {
                                    Toast.makeText(
                                        this,
                                        "Login failed: ${exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
            }
        }
    }
}
