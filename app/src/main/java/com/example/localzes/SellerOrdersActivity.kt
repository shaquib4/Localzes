package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterSellerOrders
import com.example.localzes.Modals.ModelOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SellerOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerShopOrders: RecyclerView
    private lateinit var sellerOrderAdapter: AdapterSellerOrders
    private lateinit var mSellerOrders: List<ModelOrderDetails>
    private lateinit var sellerOrderDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var imgBackPending:ImageView
    private lateinit var relativeOrders:RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_orders)
        recyclerShopOrders = findViewById(R.id.recyclerShopOrders)
        imgBackPending=findViewById(R.id.imgBackPending)
        relativeOrders=findViewById(R.id.rl_Pending_Orders)
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
                        i.child("deliveryAddress").value.toString(),
                        i.child("paymentMode").value.toString()
                    )
                    if (i.child("orderStatus").value.toString() == "Pending") {
                        (mSellerOrders as ArrayList<ModelOrderDetails>).add(obj)
                    }
                }
                if(mSellerOrders.isEmpty()){
                    recyclerShopOrders.visibility= View.GONE
                }
                else{
                    relativeOrders.visibility=View.GONE
                    recyclerShopOrders.visibility=View.VISIBLE
                    sellerOrderAdapter =
                        AdapterSellerOrders(
                            this@SellerOrdersActivity,
                            mSellerOrders
                        )
                    recyclerShopOrders.adapter = sellerOrderAdapter
                }
            }
        })
    }
}