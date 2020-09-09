package com.kotlin.connectapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.net.HttpURLConnection
import java.net.URL

class ViewConnectActivity : AppCompatActivity() {

    var statusTextView: TextView? = null
    var connectImageView: ImageView? = null
    var auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_connect)

        setTitle("Inbox")
        //FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()

        statusTextView = findViewById(R.id.statusTextView)
        connectImageView = findViewById(R.id.connectImageView)

        statusTextView?.text = intent.getStringExtra("status")

        val task = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()
            connectImageView?.setImageBitmap(myImage)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class ImageDownloader : AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val `in` = connection.inputStream
                return BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        FirebaseDatabase.getInstance().getReference().child("users").child(auth.currentUser?.uid.toString()).child("connects").child(intent.getStringExtra("connectKey")).removeValue()
        FirebaseStorage.getInstance().getReference().child("images").child(intent.getStringExtra("imageName")).delete()
    }

}