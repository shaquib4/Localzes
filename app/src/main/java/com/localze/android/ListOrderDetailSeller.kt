package com.localze.android

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.localze.android.Adapters.AdapterSellerListOrder
import com.localze.android.Modals.ModelList
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list_order_detail_seller.*
import org.json.JSONObject
import util.ConnectionManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ListOrderDetailSeller : AppCompatActivity() {
    private lateinit var orderIdTv: TextView
    private lateinit var orderDateTv: TextView
    private lateinit var orderStatusTv: TextView
    private lateinit var itemsTv: TextView
    private lateinit var amountTv: TextView
    private lateinit var deliveryAddressTv: TextView
    private lateinit var recyclerOrderedList: RecyclerView
    private lateinit var adapterListOrder: AdapterSellerListOrder
    private lateinit var imgBackList: ImageView
    private var bools: Boolean = false
    private lateinit var imgListEdit: ImageView
    private lateinit var list: List<ModelList>
    private lateinit var etDeliveryList: EditText
    private lateinit var deliveryFee: TextView
    private lateinit var paymentStatus: TextView
    private lateinit var progressDialog: ProgressDialog
    private var bool = true
    private var orderId = ""
    private var orderBy = ""
    private var orderTo = ""
    private var oStatus = ""
    private var newBool: Boolean = false
    var totalCost: Double = 0.00
    var totalWith: Double = 0.00
    private var selectedReason: String = ""
    private var customerMobileNo: String = ""
    private lateinit var imgMakeCustomerCall: ImageView
    private lateinit var checkboxListComplete: CheckBox
    private lateinit var llAcceptConfirm: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_order_detail_seller)
        orderIdTv = findViewById(R.id.txtListOrderId)
        orderDateTv = findViewById(R.id.txtListOrderDate)
        orderStatusTv = findViewById(R.id.txtListOrderStatus)
        itemsTv = findViewById(R.id.txtListItems)
        amountTv = findViewById(R.id.txtListOrderCost)
        deliveryAddressTv = findViewById(R.id.txtListOrderDeliveryAddress)
        recyclerOrderedList = findViewById(R.id.recyclerOrderedSellerItem)
        imgMakeCustomerCall = findViewById(R.id.imageMakeCallCustomer)
        checkboxListComplete = findViewById(R.id.checkbox_List_Completed)
        imgListEdit = findViewById(R.id.imgListEdit)
        deliveryFee = findViewById(R.id.deliverySellerCharge)
        paymentStatus = findViewById(R.id.paymentStatusSeller)
        etDeliveryList = findViewById(R.id.etDeliveryChargeList)
        imgBackList = findViewById(R.id.imgBackListOrderDetails)
        llAcceptConfirm = findViewById(R.id.rlAcceptConfirm)
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        list = ArrayList<ModelList>()
        recyclerOrderedList.layoutManager = LinearLayoutManager(this)
        orderId = intent.getStringExtra("orderId").toString()
        orderBy = intent.getStringExtra("orderBy").toString()
        orderTo = intent.getStringExtra("orderTo").toString()

        val refs = FirebaseDatabase.getInstance().reference.child("users").child(orderBy.toString())
            .child("current_address")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val latitude = snapshot.child("latitude").value.toString()
                    val longitude = snapshot.child("longitude").value.toString()
                    btnDirListSeller.setOnClickListener {
                        val uri = Uri.parse("google.navigation:q=$latitude,$longitude")
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = uri
                        startActivity(intent)
                    }
                }
            })
        val databaseRef: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(orderTo)
                .child("OrdersLists")
        val newRef =
            FirebaseDatabase.getInstance().reference.child("seller").child(orderTo)
                .child("OrdersLists")
                .child(orderId)
        newRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                customerMobileNo = snapshot.child("orderByMobile").value.toString()
                if (snapshot.child("deliveryFee").exists()) {
                    bools = true
                }
                if (bools == true) {
                    amountTv.text = "₹${snapshot.child("orderCost").value.toString()}"
                }
            }

        })
        imgBackList.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
        imgMakeCustomerCall.setOnClickListener {
            makePhoneCallCustomer()
        }
        orderIdTv.text = "OD${orderId}"
        val ref: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(orderBy)
                .child("MyOrderList")
        retryListOrdersDetails.setOnClickListener {
            this.recreate()
        }
        if (ConnectionManager().checkConnectivity(this)) {
            //rl_ListSeller.visibility = View.VISIBLE
            // rl_ListOrderDetails.visibility = View.GONE
            databaseRef.child(orderId).child("ListItems")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        bool = true
                        var finalPriceList = arrayListOf<Double>()
                        totalCost = 0.0
                        for (i in snapshot.children) {
                            val itemC = i.child("itemCost").value.toString()
                            if (itemC == "") {
                                bool = false
                            }
                            finalPriceList.clear()
                            val itemId = i.child("itemId").value.toString()
                            val itemCost = i.child("itemCost").value.toString()
                            val itemName = i.child("itemName").value.toString()
                            val itemQuantity = i.child("itemQuantity").value.toString()
                            try {
                                finalPriceList.add(itemCost.toDouble())
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            for (j in finalPriceList) {
                                totalCost += j
                            }
                        }
                        if (bools == false) {
                            amountTv.text = "₹${totalCost}"
                        }
                        itemsTv.text = snapshot.childrenCount.toString()
                        val headers = HashMap<String, Any>()
                        try {
                            headers["orderCost"] =
                                (totalCost + etDeliveryList.text.toString().toDouble()).toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        databaseRef.child(orderId).updateChildren(headers)
                            .addOnSuccessListener {
                                ref.child(orderId).updateChildren(headers)
                            }
                    }
                })
        } else {
            //  rl_ListSeller.visibility = View.GONE
            //  rl_ListOrderDetails.visibility = View.VISIBLE
        }
        databaseRef.child(orderId).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val orderStatus = snapshot.child("orderStatus").value.toString()
                val orderTime = snapshot.child("orderTime").value.toString()
                val sdf = SimpleDateFormat("dd/MM/yyyy,hh:mm a")
                val date = Date(orderTime.toLong())
                val formattedDate = sdf.format(date)
                oStatus = orderStatus
                when (orderStatus) {
                    "Pending" -> {
                        checkboxListComplete.visibility = View.GONE
                        orderStatusTv.setTextColor(resources.getColor(R.color.colorAccent))
                        imgListEdit.visibility = View.GONE
                        llAcceptConfirm.visibility = View.VISIBLE
                    }
                    "Accepted" -> {
                        checkboxListComplete.visibility = View.VISIBLE
                        orderStatusTv.setTextColor(resources.getColor(R.color.acidGreen))
                        imgListEdit.visibility = View.VISIBLE
                        llAcceptConfirm.visibility = View.GONE
                        imgListEdit.setOnClickListener {
                            editOrderStatusDialog()
                        }
                    }
                    "Rejected due to Item is Out Of Stock" -> {
                        checkboxListComplete.visibility = View.GONE
                        orderStatusTv.setTextColor(resources.getColor(R.color.red))
                        imgListEdit.visibility = View.GONE
                        llAcceptConfirm.visibility = View.GONE
                    }
                    "Rejected due to Shop is closed Now" -> {
                        checkboxListComplete.visibility = View.GONE
                        orderStatusTv.setTextColor(resources.getColor(R.color.red))
                        imgListEdit.visibility = View.GONE
                        llAcceptConfirm.visibility = View.GONE
                    }
                    "Rejected due to Others" -> {
                        checkboxListComplete.visibility = View.GONE
                        orderStatusTv.setTextColor(resources.getColor(R.color.red))
                        imgListEdit.visibility = View.GONE
                        llAcceptConfirm.visibility = View.GONE
                    }
                    "Out For Delivery" -> {
                        checkboxListComplete.visibility = View.VISIBLE
                        orderStatusTv.setTextColor(resources.getColor(R.color.green))
                        imgListEdit.visibility = View.VISIBLE
                        llAcceptConfirm.visibility = View.GONE
                        imgListEdit.setOnClickListener {
                            newEditOrderStatusDialog()
                        }
                    }
                    "Completed" -> {
                        orderStatusTv.setTextColor(resources.getColor(R.color.green))
                        checkboxListComplete.visibility = View.GONE
                        imgListEdit.visibility = View.GONE
                        llAcceptConfirm.visibility = View.GONE
                    }
                }
                orderStatusTv.text = orderStatus
                orderDateTv.text = formattedDate

                deliveryAddressTv.text = snapshot.child("deliveryAddress").value.toString()
                if (snapshot.child("deliveryFee").exists()) {
                    deliveryFee.text = "₹" + snapshot.child("deliveryFee").value.toString()
                    paymentStatus.text = snapshot.child("paymentMode").value.toString()
                } else {
                    paymentStatus.text = "Unpaid"
                    etDeliveryList.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {

                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            deliveryFee.text = "₹${s.toString()}"
                        }
                    })
                }
            }
        })
        reject.setOnClickListener {
            val newBuilder = AlertDialog.Builder(this)
            newBuilder.setTitle("Choose A Reason")
            val reasons =
                arrayOf("Item is Out Of Stock", "Shop is closed Now", "Others")
            newBuilder.setSingleChoiceItems(reasons, -1) { dialog, which ->
                val selected = reasons[which]
                selectedReason = selected
                val headers = HashMap<String, Any>()
                headers["listStatus"] = "Rejected"
                headers["orderStatus"] = "Rejected due to $selectedReason"
                headers["orderCost"] = "0.0"
                headers["deliveryFee"] = "0.0"

                ref.child(orderId).updateChildren(headers).addOnCompleteListener {
                    if (it.isSuccessful) {
                        prepareNotificationMessage(orderId, "Order Rejected due to $selectedReason")
                        databaseRef.child(orderId).updateChildren(headers)
                            .addOnCompleteListener {
                                databaseRef.child(orderId).child("ListItems")
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
                                            adapterListOrder = AdapterSellerListOrder(
                                                this@ListOrderDetailSeller,
                                                list,
                                                orderId,
                                                orderBy,
                                                "Rejected",
                                                orderTo
                                            )
                                            recyclerOrderedList.adapter =
                                                adapterListOrder
                                        }
                                    })
                            }
                    }
                }
                dialog.dismiss()
            }
            newBuilder.create().show()
        }

        acceptConfirm.setOnClickListener {
            if (etDeliveryList.text.toString().isNotEmpty()) {

                if (ConnectionManager().checkConnectivity(this)) {
                    //  rl_ListSeller.visibility = View.VISIBLE
                    //  rl_ListOrderDetails.visibility = View.GONE
                    val builder = AlertDialog.Builder(this)
                    val dialog = builder.show()
                    builder.setTitle("Confirmation")
                    builder.setMessage("Are you sure to confirm this list Order")
                    builder.setPositiveButton("Yes") { text, listener ->
                        dialog.dismiss()
                        progressDialog.setMessage("Processing.....")
                        progressDialog.show()
                        if (bool) {
                            try {
                                totalWith = totalCost + etDeliveryList.text.toString().toDouble()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            val headers = HashMap<String, Any>()
                            headers["listStatus"] = "Confirm"
                            headers["orderStatus"] = "Accepted"
                            headers["orderCost"] =
                                totalWith.toString()
                            headers["deliveryFee"] = etDeliveryList.text.toString()

                            ref.child(orderId).updateChildren(headers).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    prepareNotificationMessage(orderId, "Order has been Confirmed")
                                    databaseRef.child(orderId).updateChildren(headers)
                                        .addOnCompleteListener {
                                            databaseRef.child(orderId).child("ListItems")
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
                                                        adapterListOrder = AdapterSellerListOrder(
                                                            this@ListOrderDetailSeller,
                                                            list,
                                                            orderId,
                                                            orderBy,
                                                            "Accepted",
                                                            orderTo
                                                        )
                                                        recyclerOrderedList.adapter =
                                                            adapterListOrder
                                                    }
                                                })
                                        }
                                }
                            }


                        } else {
                            dialog.dismiss()
                            Toast.makeText(this, "Some fields are empty", Toast.LENGTH_LONG).show()
                        }
                        progressDialog.dismiss()
                    }
                    builder.setNegativeButton("No") { text, listener ->
                        dialog.dismiss()
                    }
                    builder.create().show()
                } else {
                    //  rl_ListSeller.visibility = View.GONE
                    //  rl_ListOrderDetails.visibility = View.VISIBLE
                }

            } else {
                Toast.makeText(this, "Please enter delivery charge", Toast.LENGTH_SHORT).show()
            }

        }

        if (ConnectionManager().checkConnectivity(this)) {
            //  rl_ListSeller.visibility = View.VISIBLE
            //  rl_ListOrderDetails.visibility = View.GONE
            databaseRef.child(orderId).child("ListItems")
                .addListenerForSingleValueEvent(object : ValueEventListener {
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
                        adapterListOrder =
                            AdapterSellerListOrder(
                                this@ListOrderDetailSeller,
                                list,
                                orderId,
                                orderBy,
                                oStatus,
                                orderTo
                            )
                        recyclerOrderedList.adapter = adapterListOrder
                    }
                })
        } else {
            //  rl_ListSeller.visibility = View.GONE
            // rl_ListOrderDetails.visibility = View.VISIBLE
        }

    }

    private fun makePhoneCallCustomer() {
        val number = customerMobileNo
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        val chooser = Intent.createChooser(intent, "Call Action Using")
        startActivity(chooser)
    }


    private fun newEditOrderStatusDialog() {
        val options = arrayOf("Rejected", "Payment Received")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Order Status")
        builder.setSingleChoiceItems(options, -1) { dialog, which ->
            val selectedItem = options[which]
            when (selectedItem) {
                "Rejected" -> {
                    dialog.dismiss()
                    val newBuilder = AlertDialog.Builder(this)
                    newBuilder.setTitle("Choose A Reason")
                    val reasons =
                        arrayOf("Item is Out Of Stock", "Shop is closed Now", "Others")
                    newBuilder.setSingleChoiceItems(reasons, -1) { dialog, which ->
                        val selected = reasons[which]
                        selectedReason = selected
                        dialog.dismiss()
                    }
                    newBuilder.create().show()
                    editOrderStatus("$selectedItem due to $selectedReason")
                }
                "Payment Received" -> {
                    newBool = true
                    dialog.dismiss()
                }
                else -> {
                    editOrderStatus(selectedItem)
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }

    private fun editOrderStatusDialog() {
        val options = if (!newBool) {
            arrayOf("Rejected", "Out For Delivery", "Payment Received")
        } else {
            arrayOf("Rejected", "Out For Delivery")
        }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Order Status")
        builder.setSingleChoiceItems(options, -1) { dialog, which ->
            val selectedItem = options[which]
            when (selectedItem) {
                "Payment Received" -> {
                    newBool = true
                    dialog.dismiss()
                }
                "Rejected" -> {
                    dialog.dismiss()
                    val newBuilder = AlertDialog.Builder(this)
                    newBuilder.setTitle("Choose A Reason")
                    val reasons =
                        arrayOf("Item is Out Of Stock", "Shop is closed Now", "Others")
                    newBuilder.setSingleChoiceItems(reasons, -1) { dialog, which ->
                        val selected = reasons[which]
                        selectedReason = selected
                        editOrderStatus("$selectedItem due to $selectedReason")
                        dialog.dismiss()
                    }
                    newBuilder.create().show()
                }
                else -> {
                    editOrderStatus(selectedItem)
                    dialog.dismiss()
                }
            }
        }
        builder.create().show()
    }

    private fun editOrderStatus(selectedItem: String) {
        val headers = hashMapOf<String, Any>()
        headers["orderStatus"] = selectedItem
        val ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("seller")
        ref.child(orderBy).child("OrdersLists").child(orderId).updateChildren(headers)
            .addOnSuccessListener {
                Toast.makeText(this, "Order has been $selectedItem", Toast.LENGTH_SHORT).show()
                val message = "Order has been $selectedItem"
                val reference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("users").child(orderTo)
                reference.child("MyOrderList").child(orderId).updateChildren(headers)
                prepareNotificationMessage(orderId, message)
            }
    }

    private fun prepareNotificationMessage(orderId: String, message: String) {
        val NOTIFICATION_TOPIC = "/topics/PUSH_NOTIFICATIONS"
        val NOTIFICATION_TITLE = message
        var NOTIFICATION_MESSAGE = ""
        when (message) {
            "Order has been Accepted" -> {
                NOTIFICATION_MESSAGE = "Yay! Your order has been accepted"
            }
            "Order has been Rejected due to $selectedReason" -> {
                NOTIFICATION_MESSAGE =
                    "Sorry for the inconvenience.Your order is rejected due to $selectedReason"
            }
            "Order has been Confirmed" -> {
                NOTIFICATION_MESSAGE = "Your order costs only ₹${totalWith}"
            }
            "Order has been Out For Delivery" -> {
                NOTIFICATION_MESSAGE = "Congratulations! Your order is on the way"
            }
        }
        val NOTIFICATION_TYPE = "OrderListStatusChanged"
        val notificationJs = JSONObject()
        val notificationBodyJs = JSONObject()
        try {
            notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
            notificationBodyJs.put("buyerId", orderBy)
            notificationBodyJs.put("sellerUid", orderTo)
            notificationBodyJs.put("orderId", orderId)
            notificationBodyJs.put("totalCost", totalWith.toString())
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
        val jsonObjectRequest =
            object : JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJs,
                Response.Listener {
                    val intent = Intent(this, Home_seller::class.java)
                    startActivity(intent)
                    finish()

                }, Response.ErrorListener {

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
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }

    fun onListCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                R.id.checkbox_List_Completed -> {
                    if (checked) {
                        val builder = android.app.AlertDialog.Builder(this)
                        val new = builder.show()
                        builder.setTitle("Confirmation")
                        builder.setMessage("Are you sure your order has been successfully completed")
                        builder.setPositiveButton("Ok") { text, listener ->
                            val userMap = HashMap<String, Any>()
                            userMap["orderStatus"] = "Completed"
                            val databaseNewReference: DatabaseReference =
                                FirebaseDatabase.getInstance().reference.child("seller")
                            databaseNewReference.child(orderBy).child("OrdersLists").child(orderId)
                                .updateChildren(userMap).addOnSuccessListener {
                                    val dataRef =
                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child(orderBy).child("MyOrderList").child(orderId)
                                    dataRef.updateChildren(userMap)
                                }

                            new.dismiss()
                            view.visibility = View.GONE
                        }
                        builder.setNegativeButton("Cancel") { text, listener ->
                            view.isChecked = false
                            new.dismiss()
                        }
                        builder.create().show()
                    }
                }
            }
        }
    }
}