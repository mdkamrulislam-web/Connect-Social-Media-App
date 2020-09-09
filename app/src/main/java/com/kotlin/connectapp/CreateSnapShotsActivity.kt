package com.kotlin.connectapp

//import android.support.v7.app.AppCompatActivity
//import android.support.annotation.NonNull
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapShotsActivity : AppCompatActivity() {

    var chooseImageView: ImageView? = null
    var statusEditText: EditText? = null
    var imageName = UUID.randomUUID().toString() + ".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap_shots)

        title = "Connect Friends"

        chooseImageView = findViewById(R.id.chooseImageView)
        statusEditText = findViewById(R.id.statusEditText)
    }

    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImageClicked(view: View){
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val selectedImage = data!!.data
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                chooseImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    fun connectPeopleClicked(view: View){
        // Get the data from an ImageView as bytes

        // Get the data from an ImageView as bytes
        chooseImageView?.setDrawingCacheEnabled(true)
        chooseImageView?.buildDrawingCache()
        val bitmap = (chooseImageView?.getDrawable() as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()


        val uploadTask: UploadTask = FirebaseStorage.getInstance().reference.child("Images").child(
            imageName
        ).putBytes(data)
        uploadTask.addOnFailureListener(OnFailureListener {
            Toast.makeText(applicationContext, "We could upload the image :(", Toast.LENGTH_SHORT)
                .show()
        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot>() { taskSnapshot ->
            if (taskSnapshot.metadata != null) {
                if (taskSnapshot.metadata!!.reference != null) {
                    var result: Task<Uri> = taskSnapshot.storage.downloadUrl;
                    result.addOnSuccessListener(OnSuccessListener<Uri>() { url ->
                        var imageUrl: String = url.toString();
                        Log.i("URL", imageUrl.toString())

                        var intent = Intent(this, ChooseUserActivity::class.java)
                        intent.putExtra("imageURL", imageUrl)
                        intent.putExtra("imageName", imageName)
                        intent.putExtra("status", statusEditText?.text.toString())
                        startActivity(intent)
                        Toast.makeText(this, "You selected An Image. Please Select The Person You Want to Send!", Toast.LENGTH_LONG).show()
                    });
                }

            }
        });
    }
}