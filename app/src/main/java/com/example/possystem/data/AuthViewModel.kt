package com.example.possystem.data

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.possystem.models.UserModel
import com.example.possystem.navigation.ROUTE_DASHBOARD
import com.example.possystem.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // ─── Sign Up ──────────────────────────────────────────────────────────────

    fun signup(
        username: String,
        email: String,
        phoneNumber: String,           // FIX 1: added missing phoneNumber parameter
        password: String,
        confirmpassword: String,       // FIX 2: kept as confirmpassword to match RegisterScreen call
        navController: NavController,
        context: Context
    ) {
        if (username.isBlank() || email.isBlank() || phoneNumber.isBlank() ||
            password.isBlank() || confirmpassword.isBlank()
        ) {
            Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_LONG).show()
            return
        }
        if (password != confirmpassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    val user = UserModel(
                        username = username,
                        email = email,
                        userId = userId,
                        phoneNumber = phoneNumber  // FIX 3: now saved to UserModel
                    )
                    saveUserToDatabase(user, navController, context)
                } else {
                    Toast.makeText(
                        context,
                        task.exception?.message ?: "Registration failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // ─── Save User to Firebase Database ──────────────────────────────────────

    private fun saveUserToDatabase(
        user: UserModel,
        navController: NavController,
        context: Context
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Users/${user.userId}")
        dbRef.setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "User registered successfully", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_DASHBOARD) {  // FIX 4: navigate to DASHBOARD after signup
                    popUpTo(0)
                }
            } else {
                Toast.makeText(
                    context,
                    task.exception?.message ?: "Failed to save user",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // ─── Login ────────────────────────────────────────────────────────────────
    // FIX 5: login() function was completely missing — added it back

    fun login(
        email: String,
        password: String,
        navController: NavController,
        context: Context
    ) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Email and password are required", Toast.LENGTH_LONG).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Login successful", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_DASHBOARD) {  // FIX 6: navigates to Dashboard
                        popUpTo(0)
                    }
                } else {
                    Toast.makeText(
                        context,
                        task.exception?.message ?: "Login failed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // ─── Logout ───────────────────────────────────────────────────────────────

    fun logout(navController: NavController, context: Context) {
        auth.signOut()
        navController.navigate(ROUTE_LOGIN) {
            popUpTo(0)
        }
        Toast.makeText(context,"log out successfully", Toast.LENGTH_SHORT).show()
    }
}