package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserProductsActivity : AppCompatActivity() {
    var shopId:String?="100"
    private lateinit var mUserProductDatabase:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_products)
        shopId=intent.getStringExtra("ShopId")
        mUserProductDatabase=FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
    }
}