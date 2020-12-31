package com.example.localzes

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.example.localzes.Adapters.AdapterCreateList
import com.example.localzes.Modals.ModalSellerOrderList
import com.example.localzes.Modals.ModelList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_list.*
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley

class CreateList : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var userDatabase: DatabaseReference
    private lateinit var createListAdapter: AdapterCreateList
    private lateinit var list: List<ModelList>
    private lateinit var listRecycler: RecyclerView
    private lateinit var shopDatabase: DatabaseReference
    private var shopId: String? = "200"
    private var bool = false
    private var fool = true
    private var tool = false
    private lateinit var progressDialog: ProgressDialog
    private var orderByName: String? = "100"
    private var orderByMobile: String? = "300"
    private var deliveryAddress: String? = "400"
    private var userId: String? = "500"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_list)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        shopId = intent.getStringExtra("ShopListId")
        orderByName = intent.getStringExtra("orderByName")
        orderByMobile = intent.getStringExtra("orderByMobile")
        deliveryAddress = intent.getStringExtra("delivery")
        userId = intent.getStringExtra("userId")
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        list = ArrayList<ModelList>()
        listRecycler = findViewById(R.id.recycler_list)
        listRecycler.layoutManager = LinearLayoutManager(this)
        userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(uid)
        shopDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
        shopDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val shopName = snapshot.child("shop_name").value.toString()
                val mobileNo = snapshot.child("phone").value.toString()
            }

        })
        userDatabase.child("OrderList").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                bool = true
                for (i in snapshot.children) {


                    val itemId = i.child("itemId").value.toString()
                    val itemName = i.child("itemName").value.toString()
                    val itemQuan = i.child("itemQuantity").value.toString()

                    if (itemQuan == "" || itemName == "") {
                        bool = false

                    }

                }

            }
        })

        userDatabase.child("OrderList").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                fool = false
                if (snapshot.exists()) {
                    fool = true
                }
            }
        })


        userDatabase.child("OrderList").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                for (i in snapshot.children) {

                    val obj = ModelList(
                        i.child("itemId").value.toString(),
                        i.child("itemName").value.toString(),
                        i.child("itemQuantity").value.toString(),
                        i.child("itemCost").value.toString(),
                        i.child("shopId").value.toString()
                    )
                    (list as ArrayList<ModelList>).add(obj)

                }
                createListAdapter = AdapterCreateList(this@CreateList, list)
                listRecycler.adapter = createListAdapter
            }
        })
        btnAdd.setOnClickListener {
            val timestamp = System.currentTimeMillis().toString()
            val headers = HashMap<String, String>()
            headers["itemId"] = timestamp
            headers["itemName"] = ""
            headers["itemQuantity"] = ""
            headers["itemCost"] = ""
            headers["shopId"] = shopId.toString()
            userDatabase.child("OrderList").child(timestamp).setValue(headers)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        this.recreate()
                    }
                }
        }

        btnCn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val dialog = builder.show()
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure your list is complete.Please click Yes to continue")
            builder.setPositiveButton("Yes") { text, listener ->
                if (bool && fool) {
                    dialog.dismiss()
                    progressDialog.setMessage("Placing Your Order....")
                    progressDialog.show()
                    userDatabase.child("OrderList")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                (list as ArrayList<ModelList>).clear()
                                for (i in snapshot.children) {

                                    val obj = ModelList(
                                        i.child("itemId").value.toString(),
                                        i.child("itemName").value.toString(),
                                        i.child("itemQuantity").value.toString(),
                                        i.child("itemCost").value.toString(),
                                        i.child("shopId").value.toString()
                                    )
                                    (list as ArrayList<ModelList>).add(obj)

                                }
                                createListAdapter = AdapterCreateList(this@CreateList, list)
                                listRecycler.adapter = createListAdapter
                            }
                        })

                    val timestamp = System.currentTimeMillis().toString()
                    val orderId = timestamp
                    val orderTime = timestamp
                    val orderStatus = "Pending"
                    val orderCost = ""
                    val orderBy = userId.toString()
                    val orderTo = shopId.toString()
                    val deliveryAddress = deliveryAddress.toString()
                    val totalItems = list.size.toString()
                    val listStatus = ""
                    val obj = ModalSellerOrderList(
                        orderId,
                        orderTime,
                        orderStatus,
                        orderCost,
                        orderBy,
                        orderTo,
                        deliveryAddress,
                        totalItems,
                        listStatus,
                        orderByName.toString(),
                        orderByMobile.toString()
                    )
                    val dataReference: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(userId.toString())
                            .child("MyOrderList")
                    val ref: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("seller")
                            .child(shopId.toString()).child("OrdersLists")

                    ref.child(orderId).setValue(obj)
                    dataReference.child(orderId).setValue(obj).addOnCompleteListener {
                        if (it.isSuccessful) {
                            for (i in 0 until list.size) {
                                val itemId = list[i].itemId
                                val itemName = list[i].itemName
                                val itemQuantity = list[i].itemQuantity
                                val itemCost = list[i].itemCost
                                val headers = HashMap<String, String>()
                                headers["itemId"] = itemId
                                headers["itemName"] = itemName
                                headers["itemQuantity"] = itemQuantity
                                headers["itemCost"] = itemCost
                                ref.child(orderId).child("ListItems").child(itemId)
                                    .setValue(headers)
                                dataReference.child(orderId).child("ListItems").child(itemId)
                                    .setValue(headers)
                            }
                            userDatabase.child("OrderList").removeValue()
                            prepareNotificationMessage(orderId)
                        }
                    }

                } else {
                    dialog.dismiss()
                    Toast.makeText(this@CreateList, "Some fields are empty", Toast.LENGTH_LONG)
                        .show()
                }
                progressDialog.dismiss()
            }
            builder.setNegativeButton("No") { text, listener ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    private fun prepareNotificationMessage(orderId: String) {
        val NOTIFICATION_TOPIC = "/topics/PUSH_NOTIFICATIONS"
        val NOTIFICATION_TITLE = "New List Order has been received"
        val NOTIFICATION_MESSAGE = "Congratulations....!You received a new list order"
        val NOTIFICATION_TYPE = "New Order List"
        val notificationJs = JSONObject()
        val notificationBodyJs = JSONObject()
        try {
            notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
            notificationBodyJs.put("buyerId", userId.toString())
            notificationBodyJs.put("sellerUid", shopId.toString())
            notificationBodyJs.put("orderId", orderId)
            notificationBodyJs.put("notificationTitle", NOTIFICATION_TITLE)
            notificationBodyJs.put("notificationMessage", NOTIFICATION_MESSAGE)
            notificationJs.put("to", NOTIFICATION_TOPIC)
            notificationJs.put("data", notificationBodyJs)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        sendFcmNotification(notificationJs)

    }

    private fun sendFcmNotification(notificationJs: JSONObject) {
        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            notificationJs,
            Response.Listener {

            },
            Response.ErrorListener {

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] =
                    "key=AAAA0TgW0AY:APA91bGNGMLtISkxVjfP-Mvu6GCZeeTcoDzvFtUg0Pq1SrJ9SshsFXDuXR9i3-lOqtlUjVmGqmv4C0sSRbsIphiacRau5c1ERQEUBukLxV-EXGVGv1ZmTN796LyLs1Wd7s1Tnu60e_2D"
                return headers

            }
        }
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    override fun onBackPressed() {
        val intent = Intent(this, UserProductsActivity::class.java)
        startActivity(intent)
        finish()
    }
}