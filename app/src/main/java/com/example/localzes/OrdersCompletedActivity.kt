package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrdersCompletedActivity : AppCompatActivity() {
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var ordersCompletedList:List<ModelOrderDetails>
    private lateinit var recyclerOrdersCompleted:RecyclerView
    private lateinit var adapterOrdersCompleted:AdapterSellerOrders
    private lateinit var orderCompleted:RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_completed)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid
        ordersCompletedList=ArrayList<ModelOrderDetails>()
        orderCompleted=findViewById(R.id.rl_Completed_Orders)
        recyclerOrdersCompleted=findViewById(R.id.recyclerOrdersCompleted)
        recyclerOrdersCompleted.layoutManager = LinearLayoutManager(this)
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
                   if(ordersCompletedList.isEmpty()){
                       orderCompleted.visibility= View.VISIBLE
                       recyclerOrdersCompleted.visibility=View.GONE
                   }
                   else{
                       orderCompleted.visibility=View.GONE
                       recyclerOrdersCompleted.visibility=View.VISIBLE
                   }
                   adapterOrdersCompleted=AdapterSellerOrders(this@OrdersCompletedActivity,ordersCompletedList)
                   recyclerOrdersCompleted.adapter=adapterOrdersCompleted
               }
            }
        })
    }
}