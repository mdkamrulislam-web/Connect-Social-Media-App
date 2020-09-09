package com.kotlin.connectapp

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    var auth: FirebaseAuth = Firebase.auth
    // Initialize Firebase Auth
    //auth = Firebase.auth

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "Welcome to CONNECT!", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setTitle("CONNECT")

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if (auth.currentUser != null){
            logIn()
        }
    }
//Toast.makeText(this, "A username and a password are required.",Toast.LENGTH_SHORT).show();
    fun signupLoginClicked(view: View){
        // Check if we can login the user
        auth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //If login is a success
                    logIn()
                    Toast.makeText(this, "You Successfully Logged In!", Toast.LENGTH_SHORT).show()
                }
                else {
                    // If login fails
                    auth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString()).addOnCompleteListener(this) {task ->
                        if (task.isSuccessful){
                            // Add to database  // problem here
                            FirebaseDatabase.getInstance().reference.child("users").child(task.result!!.user?.uid!!).child("email").setValue(emailEditText?.text.toString())
                            logIn()
                            Toast.makeText(this, "You Successfully Signed Up!", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(this, "Invaild E-mail Address or Password! Please Try Again!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    fun logIn(){
        // Move to the Connect Activity
        val intent = Intent(this, ConnectActivity::class.java)
        startActivity(intent)
    }
}

//private lateinit