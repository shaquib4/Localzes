package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterUserOrderList
import com.example.localzes.Modals.ModelList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UserListOrderDetails : AppCompatActivity() {
    private lateinit var orderListIdUser: TextView
    private lateinit var orderDateListUser: TextView
    private lateinit var orderStatusListUser: TextView
    private lateinit var shopNameOrderList: TextView
    private lateinit var itemsOrderList: TextView
    private lateinit var amountOrderList: TextView
    private lateinit var deliveryAddressOrder: TextView
    private lateinit var shopAddressOrder: TextView
    private lateinit var orderedItemList: List<ModelList>
    private lateinit var recyclerOrderList:RecyclerView
    private lateinit var adapteruserOrder:AdapterUserOrderList
    private var orderId: String? = "200"
    private var orderToId: String? = "300"
    private lateinit var userAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list_order_details)
        orderListIdUser = findViewById(R.id.txtOrderListIdUser)
        orderDateListUser = findViewById(R.id.txtOrderListDateUser)
        orderStatusListUser = findViewById(R.id.txtOrderListStatusUser)
        shopNameOrderList = findViewById(R.id.txtShopNameOrderList)
        itemsOrderList = findViewById(R.id.txtItemsListUser)
        amountOrderList = findViewById(R.id.txtOrderListCostUser)
        deliveryAddressOrder = findViewById(R.id.txtOrderListDeliveryAddressUser)
        shopAddressOrder = findViewById(R.id.txtOrderListShopAddressUser)
        recyclerOrderList=findViewById(R.id.recycler_order_list_users)
        recyclerOrderList.layoutManager=LinearLayoutManager(this)
        orderedItemList = ArrayList<ModelList>()
        orderId = intent.getStringExtra("orderId")
        orderToId = intent.getStringExtra("orderTo")
        userAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrderList")
                .child(orderId.toString())
        databaseReference.child("ListItems").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (orderedItemList as ArrayList<ModelList>).clear()
                for (i in snapshot.children) {
                    val obj = ModelList(
                        i.child("itemId").value.toString(),
                        i.child("itemName").value.toString(),
                        i.child("itemQuantity").value.toString(),
                        i.child("itemCost").value.toString(),
                        i.child("shopId").value.toString()
                    )
                    (orderedItemList as ArrayList<ModelList>).add(obj)
                }
                adapteruserOrder=AdapterUserOrderList(this@UserListOrderDetails,orderedItemList)
                recyclerOrderList.adapter=adapteruserOrder

            }

        })
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val orderId = snapshot.child("orderId").value.toString()
                val orderTime = snapshot.child("orderTime").value.toString()
                val orderStatus = snapshot.child("orderStatus").value.toString()
                val orderCost: String = snapshot.child("orderCost").value.toString()
                val orderBy = snapshot.child("order").value.toString()
                val orderTo = snapshot.child("orderTo").value.toString()
                val sdf = SimpleDateFormat("dd/MM/yyyy,hh:mm a")
                val date = Date(orderTime.toLong())
                val formattedDate = sdf.format(date)
                when (orderStatus) {
                    "In Progress" -> {
                        orderStatusListUser.setTextColor(resources.getColor(R.color.colorAccent))
                    }
                    "Accepted" -> {
                        orderStatusListUser.setTextColor(resources.getColor(R.color.green))
                    }
                    "Out For Delivery" -> {
                        orderStatusListUser.setTextColor(resources.getColor(R.color.acidGreen))
                    }
                    "Cancelled" -> {
                        orderStatusListUser.setTextColor(resources.getColor(R.color.red))
                    }
                }
                orderListIdUser.text = "OD${orderId}"
                orderStatusListUser.text = orderStatus
                amountOrderList.text = "₹${orderCost}"
                orderDateListUser.text = formattedDate
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("seller")
                        .child(orderToId.toString())
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val shop = snapshot.child("address").value.toString()
                        val shopsName = snapshot.child("shop_name").value.toString()
                        shopNameOrderList.text = shopsName
                        shopAddressOrder.text = shop
                    }
                })
                val dataReference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("seller")
                        .child(orderToId.toString()).child("OrdersLists").child(orderId)
                dataReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val delivery = snapshot.child("deliveryAddress").value.toString()
                        deliveryAddressOrder.text = delivery
                    }

                })
            }
        })
    }
}