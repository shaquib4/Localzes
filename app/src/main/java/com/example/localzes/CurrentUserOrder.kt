package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterUserListOrderHistory
import com.example.localzes.Modals.ModalSellerOrderList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CurrentUserOrder : AppCompatActivity() {
    private lateinit var relativeCurrentList: RelativeLayout
    private lateinit var recyclerCurrentListOrders: RecyclerView
    private lateinit var currentListOrderDatabase: DatabaseReference
    private lateinit var userAuth: FirebaseAuth
    private lateinit var mOrderedList: List<ModalSellerOrderList>
    private lateinit var adapterUserListOrder: AdapterUserListOrderHistory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_user_order)
        relativeCurrentList = findViewById(R.id.rl_Current_List_orders)
        recyclerCurrentListOrders = findViewById(R.id.recyclerCurrentListOrders)
        recyclerCurrentListOrders.layoutManager = LinearLayoutManager(this)
        userAuth = FirebaseAuth.getInstance()
        mOrderedList = ArrayList<ModalSellerOrderList>()
        val user = userAuth.currentUser
        val uid = user!!.uid
        currentListOrderDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrderList")
        currentListOrderDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mOrderedList as ArrayList<ModalSellerOrderList>).clear()
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
                    if (i.child("orderStatus").value.toString() == "Pending" || i.child("orderStatus").value.toString() == "Accepted" || i.child(
                            "orderStatus"
                        ).value.toString() == "Out For Delivery"
                    ) {
                        (mOrderedList as ArrayList<ModalSellerOrderList>).add(obj)
                    }
                }
                if (mOrderedList.isEmpty()) {
                    recyclerCurrentListOrders.visibility = View.GONE
                } else {
                    relativeCurrentList.visibility = View.GONE
                    recyclerCurrentListOrders.visibility = View.VISIBLE
                    adapterUserListOrder =
                        AdapterUserListOrderHistory(this@CurrentUserOrder, mOrderedList)
                    recyclerCurrentListOrders.adapter = adapterUserListOrder
                }
            }
        })
    }
}