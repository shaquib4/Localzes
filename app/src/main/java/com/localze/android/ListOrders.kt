package com.localze.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.localze.android.Adapters.AdapterListOrder
import com.localze.android.Modals.ModalSellerOrderList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListOrders : AppCompatActivity() {
    private lateinit var listAdapter: AdapterListOrder
    private lateinit var uAuth: FirebaseAuth
    private lateinit var recycler: RecyclerView
    private lateinit var listOrders: List<ModalSellerOrderList>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_orders)
        listOrders = ArrayList<ModalSellerOrderList>()

        recycler = findViewById(R.id.recyclerOrders)
        recycler.layoutManager = LinearLayoutManager(this)
        uAuth = FirebaseAuth.getInstance()
        val user = uAuth.currentUser
        val uid = user!!.uid

        FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (listOrders as ArrayList<ModalSellerOrderList>).clear()
                    for (i in snapshot.children) {
                        val obj = ModalSellerOrderList(
                            i.child("orderId").value.toString(),
                            i.child("orderTime").value.toString(),
                            i.child("orderStatus").value.toString(),
                            i.child("orderCost").value.toString(),
                            i.child("orderBy").value.toString(),
                            i.child("orderTo").value.toString(),
                            i.child("deliveryAddress").value.toString(),
                            i.child("totalItems").value.toString(),
                            i.child("listStatus").value.toString(),
                            i.child("orderByName").value.toString(),
                            i.child("orderByMobile").value.toString(),
                            i.child("paymentMode").value.toString()
                        )
                        (listOrders as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                    listAdapter = AdapterListOrder(this@ListOrders, listOrders)
                    recycler.adapter = listAdapter
                }
            })
    }
}