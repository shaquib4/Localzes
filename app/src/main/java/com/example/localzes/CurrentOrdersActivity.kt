package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CurrentOrdersActivity : AppCompatActivity() {
    private lateinit var userCurrentOrdersHistory:List<ModelUserOrderDetails>
    private lateinit var recyclerOrderDetails: RecyclerView
    private lateinit var userCurrentActivityAdapter:AdapterUserOrderHistory
    private lateinit var currentOrderHistoryDatabase:DatabaseReference
    private lateinit var userAuth:FirebaseAuth
    private lateinit var mOrderedItem:List<ModelOrderedItems>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_orders)
        userCurrentOrdersHistory=ArrayList<ModelUserOrderDetails>()
        mOrderedItem=ArrayList<ModelOrderedItems>()
        recyclerOrderDetails=findViewById(R.id.recyclerCurrentOrders)
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        //currentOrderHistoryDatabase=FirebaseDatabase
    }
}