package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SellerOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerShopOrders: RecyclerView
    private lateinit var sellerOrderAdapter: AdapterSellerOrders
    private lateinit var mSellerOrders: List<ModelOrderDetails>
    private lateinit var sellerOrderDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_orders)
        recyclerShopOrders = findViewById(R.id.recyclerShopOrders)
        recyclerShopOrders.layoutManager = LinearLayoutManager(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        mSellerOrders = ArrayList<ModelOrderDetails>()
        sellerOrderDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Orders")
        sellerOrderDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mSellerOrders as ArrayList<ModelOrderDetails>).clear()
                for (i in snapshot.children) {
                    val obj = ModelOrderDetails(
                        i.child("orderId").value.toString(),
                        i.child("orderTime").value.toString(),
                        i.child("orderStatus").value.toString(),
                        i.child("orderCost").value.toString(),
                        i.child("orderBy").value.toString(),
                        i.child("orderTo").value.toString(),
                        i.child("orderQuantity").value.toString(),
                        i.child("deliveryAddress").value.toString()
                    )
                    (mSellerOrders as ArrayList<ModelOrderDetails>).add(obj)
                }
                sellerOrderAdapter = AdapterSellerOrders(this@SellerOrdersActivity, mSellerOrders)
                recyclerShopOrders.adapter = sellerOrderAdapter
            }
        })
    }
}