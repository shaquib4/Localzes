package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrdersCompletedActivity : AppCompatActivity() {
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var ordersCompletedList:List<ModelOrderDetails>
    private lateinit var recyclerOrdersCompleted:RecyclerView
    private lateinit var adapterOrdersCompleted:AdapterSellerOrders
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_completed)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid
        ordersCompletedList=ArrayList<ModelOrderDetails>()
        recyclerOrdersCompleted=findViewById(R.id.recyclerOrdersCompleted)
        orderDatabaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        orderDatabaseReference.child("Orders").orderByChild("orderStatus").equalTo("Completed").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
               if(snapshot.exists()){
                   (ordersCompletedList as ArrayList<ModelOrderDetails>).clear()
                   for(i in snapshot.children){
                       val obj = ModelOrderDetails(
                           i.child("orderId").value.toString(),
                           i.child("orderTime").value.toString(),
                           i.child("orderStatus").value.toString(),
                           i.child("orderCost").value.toString(),
                           i.child("orderBy").value.toString(),
                           i.child("orderTo").value.toString(),
                           i.child("orderQuantity").value.toString()
                       )
                       (ordersCompletedList as ArrayList<ModelOrderDetails>).add(obj)
                   }
                   adapterOrdersCompleted=AdapterSellerOrders(this@OrdersCompletedActivity,ordersCompletedList)
                   recyclerOrdersCompleted.adapter=adapterOrdersCompleted
               }
            }
        })
    }
}