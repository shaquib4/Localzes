package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_list.*

class CreateList : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userDatabase: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_list)
        auth= FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid

        btnAdd.setOnClickListener {
            userDatabase=FirebaseDatabase.getInstance().reference.child("users").child(uid)
        }
    }
}