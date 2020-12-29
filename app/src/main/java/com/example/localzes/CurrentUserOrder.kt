package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.ModalSellerOrderList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class CurrentUserOrder : AppCompatActivity() {
    private lateinit var relativeCurrentList:RelativeLayout
    private lateinit var recyclerCurrentListOrders:RecyclerView
    private lateinit var currentListOrderDatabase:DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private lateinit var mOrderedList:List<ModalSellerOrderList>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_user_order)
        relativeCurrentList=findViewById(R.id.rl_Current_List_orders)
        recyclerCurrentListOrders=findViewById(R.id.recyclerCurrentListOrders)
    }
}