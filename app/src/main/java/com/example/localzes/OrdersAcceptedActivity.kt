package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrdersAcceptedActivity : AppCompatActivity() {
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var ordersAcceptedList:List<ModelOrderDetails>
    private lateinit var recyclerOrdersAccepted:RecyclerView
    private lateinit var adapterOrderAccepted:AdapterSellerOrders
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_accepted)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid
        ordersAcceptedList=ArrayList<ModelOrderDetails>()
        recyclerOrdersAccepted=findViewById(R.id.recyclerOrdersAccepted)
        orderDatabaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Accepted").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (ordersAcceptedList as ArrayList<ModelOrderDetails>).clear()
                    for(i in snapshot.children){
                        val obj=ModelOrderDetails(
                            i.child("orderId").value.toString(),
                            i.child("orderTime").value.toString(),
                            i.child("orderStatus").value.toString(),
                            i.child("orderCost").value.toString(),
                            i.child("orderBy").value.toString(),
                            i.child("orderTo").value.toString(),
                            i.child("orderQuantity").value.toString()
                        )
                        (ordersAcceptedList as ArrayList<ModelOrderDetails>).add(obj)
                    }
                    adapterOrderAccepted=AdapterSellerOrders(this@OrdersAcceptedActivity,ordersAcceptedList)
                    recyclerOrdersAccepted.adapter=adapterOrderAccepted
                }
            }
        })
    }
}