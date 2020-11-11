package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class PastOrdersActivity : AppCompatActivity() {
    private lateinit var userPastOrderHistory:List<ModelUserOrderDetails>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_orders)
    }
}