package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterListOrder
import com.example.localzes.Adapters.AdapterSellerOrders
import com.example.localzes.Modals.ModalSellerOrderList
import com.example.localzes.Modals.ModelOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_seller_orders.*
import util.ConnectionManager

class SellerOrdersActivity : AppCompatActivity() {
    private lateinit var recyclerShopOrders: RecyclerView
    private lateinit var sellerOrderAdapter: AdapterSellerOrders
    private lateinit var mSellerOrders: List<ModelOrderDetails>
    private lateinit var sellerOrderDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var imgBackPending:ImageView
    private lateinit var relativeOrders:RelativeLayout
    private lateinit var listAdapter: AdapterListOrder
    private lateinit var listOrders:List<ModalSellerOrderList>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_orders)
        recyclerShopOrders = findViewById(R.id.recyclerShopOrders)
        imgBackPending=findViewById(R.id.imgBackPending)
        relativeOrders=findViewById(R.id.rl_Pending_Orders)
        listOrders=ArrayList<ModalSellerOrderList>()


        recyclerShopOrders.layoutManager = LinearLayoutManager(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        mSellerOrders = ArrayList<ModelOrderDetails>()
        sellerOrderDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Orders")
        rl_cartPending.setOnClickListener {
            pendingCartOrder()
        }
        rl_listPending.setOnClickListener {
            pendingListOrder()
        }


        imgBackPending.setOnClickListener {
            val intent=Intent(this,Home_seller::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun pendingListOrder() {
        txtCartPending.setTextColor(this.resources.getColor(R.color.black))
        cartPendingNo.setTextColor(this.resources.getColor(R.color.black))
        txtlistPending.setTextColor(this.resources.getColor(R.color.colorPrimary))
        listPendingNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        val user =auth.currentUser
        val uid = user!!.uid

        FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists")
            .addValueEventListener(object :ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (listOrders as ArrayList<ModalSellerOrderList>).clear()
                    for (i in snapshot.children){
                        val obj=ModalSellerOrderList(
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
                            i.child("orderByMobile").value.toString()


                        )
                        if (i.child("orderStatus").value.toString() == "Pending"){
                        (listOrders as ArrayList<ModalSellerOrderList>).add(obj)}
                    }
                    if (listOrders.isEmpty()){
                        recyclerShopOrders.visibility= View.GONE
                    }else{
                        relativeOrders.visibility=View.GONE
                        recyclerShopOrders.visibility=View.VISIBLE
                    listAdapter= AdapterListOrder(this@SellerOrdersActivity,listOrders)
                    recyclerShopOrders.adapter=listAdapter}
                }

            })
    }

    private fun pendingCartOrder() {
        txtCartPending.setTextColor(this.resources.getColor(R.color.colorPrimary))
        cartPendingNo.setTextColor(this.resources.getColor(R.color.colorPrimary))
        txtlistPending.setTextColor(this.resources.getColor(R.color.black))
        listPendingNo.setTextColor(this.resources.getColor(R.color.black))
        if(ConnectionManager().checkConnectivity(this)){
            rl_pending.visibility=View.VISIBLE
            rl_Seller_Orders_retry.visibility=View.GONE
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
                            i.child("paymentMode").value.toString(),
                            i.child("orderByName").value.toString(),
                            i.child("orderByMobile").value.toString()
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
            })}else{
            rl_pending.visibility=View.GONE
            rl_Seller_Orders_retry.visibility=View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val intent= Intent(this,Home_seller::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        pendingCartOrder()
    }
}