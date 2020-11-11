package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class PastOrdersActivity : AppCompatActivity() {
    private lateinit var userPastOrderHistory:List<ModelUserOrderDetails>
    private lateinit var recyclerOrderDetails: RecyclerView
    private lateinit var userPastOrderAdapter:AdapterUserOrderHistory
    private lateinit var pastOrderHistoryDatabase:DatabaseReference
    private lateinit var userAuth:FirebaseAuth
    private lateinit var mOrderedItem:List<ModelOrderedItems>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_orders)
        userPastOrderHistory=ArrayList<ModelUserOrderDetails>()
        recyclerOrderDetails=findViewById(R.id.recyclerPastOrders)
    }
}