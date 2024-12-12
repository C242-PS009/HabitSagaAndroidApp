package com.c242_ps009.habitsaga.ui.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.c242_ps009.habitsaga.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.cvRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etUsername.text.toString()

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                if (email.isEmpty()) binding.etEmail.error = "Email is empty"
                if (password.isEmpty()) binding.etPassword.error = "Password is empty"
                if (name.isEmpty()) binding.etUsername.error = "Username is empty"
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            val userData = hashMapOf(
                                "name" to name,
                                "coin" to 0,
                                "expPoints" to 0,
                                "equippedItemLayer1" to "",
                                "equippedItemLayer2" to ""
                            )
                            firestore.collection("users").document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(baseContext, "Register success.", Toast.LENGTH_SHORT).show()

                                    auth.signOut()
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding user data", e)
                                    Toast.makeText(baseContext, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            Toast.makeText(baseContext, "The email address is already in use", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", exception)
                            Toast.makeText(baseContext, "Register failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }
    }
}