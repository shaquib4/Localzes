package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Seller_Products : AppCompatActivity() {
    private lateinit var productDatabaseRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller__products)
        productDatabaseRef=FirebaseDatabase.getInstance().reference
    }
}