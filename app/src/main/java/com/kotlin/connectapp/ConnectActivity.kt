package com.kotlin.connectapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase



class ConnectActivity : AppCompatActivity() {

    var auth: FirebaseAuth = Firebase.auth
    var connectsListView: ListView? = null
    var emails: ArrayList <String> = ArrayList()
    var connects: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect)

        setTitle("Welcome to Connect!")

        connectsListView = findViewById(R.id.connectsListView)
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        connectsListView?.adapter = adapter

        FirebaseDatabase.getInstance().getReference().child("users").child(auth.currentUser?.uid.toString()).child("connects").addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                emails.add(p0?.child("from")?.value as String)
                connects.add(p0!!)
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {
                var index = 0
                for(photo: DataSnapshot in connects){
                    if(photo.key == p0?.key){
                        connects.removeAt(index)
                        emails.removeAt(index)
                    }
                    index++
                }
                adapter.notifyDataSetChanged()
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onCancelled(p0: DatabaseError) {}

        })

        connectsListView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            var connectshot = connects.get(i)
            var intent = Intent(this, ViewConnectActivity::class.java)
            intent.putExtra("imageName", connectshot.child("imageName").value as String)
            intent.putExtra("imageURL", connectshot.child("imageURL").value as String)
            intent.putExtra("status", connectshot.child("status").value as String)
            intent.putExtra("connectKey", connectshot.key)

            startActivity(intent)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.connectsnaps, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.createSnap) {
            var intent = Intent(this, CreateSnapShotsActivity::class.java)
            startActivity(intent)
        }
        else if (item?.itemId == R.id.logout){
            auth.signOut()
            finish()
            Toast.makeText(this, "You Logged Out! Come Back Soon!", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
        Toast.makeText(this, "You Logged Out! Come Back Soon!", Toast.LENGTH_SHORT).show()
    }

}