package com.example.cinestream.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.cinestream.Activities.LoginActivity
import com.example.cinestream.R
import com.example.cinestream.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var switchAccountButton: Button
    private lateinit var changePasswordButton: Button
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize UI elements
        usernameTextView = view.findViewById(R.id.username)
        emailTextView = view.findViewById(R.id.email)
        switchAccountButton = view.findViewById(R.id.switch_account_button)
        changePasswordButton = view.findViewById(R.id.change_password_button)
        logoutButton = view.findViewById(R.id.logout_button)

        // Load user data
        loadUserData()

        // Set up button actions
        setupButtonListeners()

        return view
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Fetch user data from Firestore
            firestore.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            usernameTextView.text = user.username
                            emailTextView.text = user.email
                        }
                    } else {
                        Toast.makeText(context, "User data not found.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Error fetching user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No authenticated user.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupButtonListeners() {
        switchAccountButton.setOnClickListener {
            Toast.makeText(context, "Switch account feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        changePasswordButton.setOnClickListener {
            val currentUser = auth.currentUser
            currentUser?.let {
                auth.sendPasswordResetEmail(it.email!!)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to send reset email: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } ?: run {
                Toast.makeText(context, "No authenticated user.", Toast.LENGTH_SHORT).show()
            }
        }

        logoutButton.setOnClickListener {
            // Create an AlertDialog for logout confirmation
            val alertDialog = android.app.AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { dialog, _ ->
                    // If user confirms, log out and redirect to LoginActivity
                    auth.signOut()
                    Toast.makeText(context, "Logged out.", Toast.LENGTH_SHORT).show()

                    // Redirect to LoginActivity
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish() // Close ProfileFragment
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Dismiss the dialog if the user cancels
                    dialog.dismiss()
                }
                .create()

            // Show the dialog
            alertDialog.show()
        }
    }
}
