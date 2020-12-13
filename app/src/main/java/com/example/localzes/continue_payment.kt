package com.example.localzes

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.localzes.Modals.ModelOrderDetails
import com.example.localzes.Modals.UserCartDetails
import com.google.firebase.database.*
import org.json.JSONObject
import java.util.ArrayList

class continue_payment : AppCompatActivity() {

    lateinit var radioGroup: RadioGroup
    lateinit var button: Button
    private lateinit var productCharges: TextView
    private lateinit var shippingCharges: TextView
    private lateinit var totalCharges: TextView
    private lateinit var totalChargesTv: TextView
    private var shopId: String? = "100"
    private var totalCost: String? = "200"
    private var totalItem: String? = "300"
    private var uid: String? = "400"
    private var deliveryAddress: String? = "500"
    private var orderByName:String?="600"
    private var orderByMobile:String?="700"
    private lateinit var progressDialog: ProgressDialog
    private lateinit var cartProducts: List<UserCartDetails>
    private lateinit var orderDetails: ModelOrderDetails
    private lateinit var imgBackContinue:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continue_payment)
        productCharges = findViewById(R.id.txtPCPrice)
        shippingCharges = findViewById(R.id.txtSCPrice)
        totalCharges = findViewById(R.id.txtTotalPayPrice)
        totalChargesTv = findViewById(R.id.txtPrice)
        imgBackContinue=findViewById(R.id.imgBackContinue)
        shopId = intent.getStringExtra("shopId")
        totalCost = intent.getStringExtra("totalCost")
        totalItem = intent.getStringExtra("totalItem")
        uid = intent.getStringExtra("orderBy")
        deliveryAddress = intent.getStringExtra("delivery")
        orderByName=intent.getStringExtra("orderByName")
        orderByMobile=intent.getStringExtra("orderByMobile")
        cartProducts = ArrayList<UserCartDetails>()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        radioGroup = findViewById(R.id.radioPayment)

        findViewById<Button>(R.id.btnPayContinue).setOnClickListener {

            val id = radioGroup.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(id)
            if (radioButton.text == " Pay on Delivery") {
                progressDialog.setMessage("Placing Your Order....")
                progressDialog.show()
                val dataReference: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("users")
                        .child(uid.toString()).child("Cart")
                dataReference.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        (cartProducts as ArrayList<UserCartDetails>).clear()
                        for (i in snapshot.children) {
                            val obj =
                                UserCartDetails(
                                    i.child("productId").value.toString(),
                                    i.child("orderBy").value.toString(),
                                    i.child("productTitle").value.toString(),
                                    i.child("priceEach").value.toString(),
                                    i.child("finalPrice").value.toString(),
                                    i.child("finalQuantity").value.toString(),
                                    i.child("orderTo").value.toString(),
                                    i.child("productImageUrl").value.toString(),
                                    i.child("sellingPrice").value.toString(),
                                    i.child("finalsellingPrice").value.toString()
                                )
                            (cartProducts as ArrayList<UserCartDetails>).add(obj)
                        }
                    }
                })
                val timestamp = System.currentTimeMillis().toString()
                val orderId = timestamp
                val orderTime = timestamp
                val orderStatus = "Pending"
                val orderCost = totalCost.toString()
                val orderBy = uid.toString()
                val orderTo = shopId.toString()
                val deliveryAddress = deliveryAddress.toString()
                orderDetails =
                    ModelOrderDetails(
                        orderId,
                        orderTime,
                        orderStatus,
                        orderCost,
                        orderBy,
                        orderTo,
                        totalItem.toString(),
                        deliveryAddress,
                        "Cash on Delivery",
                        orderByName.toString(),
                        orderByMobile.toString()
                    )

                val ref: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("seller").child(orderTo)
                        .child("Orders")
                ref.child(timestamp).setValue(orderDetails).addOnSuccessListener {
                    val reference: DatabaseReference =
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(orderBy.toString())
                            .child("MyOrders")
                    reference.child(orderId).setValue(orderDetails)
                    for (i in 0 until cartProducts.size) {
                        val productId = cartProducts[i].productId
                        val orderedBy = cartProducts[i].orderBy
                        val productTitle = cartProducts[i].productTitle
                        val priceEach = cartProducts[i].priceEach
                        val finalPrice = cartProducts[i].finalPrice
                        val finalQuantity = cartProducts[i].finalQuantity
                        val orderedTo = cartProducts[i].orderTo
                        val sellingPrice = cartProducts[i].sellingPrice
                        val headers = HashMap<String, String>()
                        headers["productId"] = productId
                        headers["orderBy"] = orderedBy
                        headers["productTitle"] = productTitle
                        headers["priceEach"] = priceEach
                        headers["finalPrice"] = finalPrice
                        headers["finalQuantity"] = finalQuantity
                        headers["orderTo"] = orderedTo
                        headers["sellingPrice"] = sellingPrice
                        ref.child(timestamp).child("Items").child(productId).setValue(headers)
                        reference.child(orderId).child("orderedItems").child(productId)
                            .setValue(headers)
                    }
                    progressDialog.dismiss()
                    prepareNotificationMessage(orderId)
                }
            } else if (radioButton.text == "Pay with Paytm") {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("shopId", shopId.toString())
                intent.putExtra("totalCost", totalCost.toString())
                intent.putExtra("orderBy", uid.toString())
                intent.putExtra("totalItem", totalItem.toString())
                intent.putExtra("delivery", deliveryAddress.toString())
                intent.putExtra("orderByName",orderByName.toString())
                intent.putExtra("orderByMobile",orderByMobile.toString())
                startActivity(intent)
                finish()
            }
        }
        productCharges.text="₹"+totalCost.toString()
        totalChargesTv.text="₹"+totalCost.toString()+"/-"
        totalCharges.text="₹"+totalCost.toString()
        imgBackContinue.setOnClickListener {
            val intent=Intent(this,Cart::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun prepareNotificationMessage(orderId: String) {
//when user places order,send notification to seller
        //prepare data for notification
        val NOTIFICATION_TOPIC =
            "/topics/PUSH_NOTIFICATIONS"//must be same as subscribed by user
        val NOTIFICATION_TITLE = "New order with $orderId has been received"
        val NOTIFICATION_MESSAGE = "Congratulations....!You received a new order"
        val NOTIFICATION_TYPE = "New Order"
        //prepare json(what to send and where to send)
        val notificationJs = JSONObject()
        val notificationBodyJs = JSONObject()
        try {
            //what to send
            notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
            notificationBodyJs.put("buyerId", uid.toString())
            notificationBodyJs.put("sellerUid", shopId.toString())
            notificationBodyJs.put("orderId", orderId)
            notificationBodyJs.put("notificationTitle", NOTIFICATION_TITLE)
            notificationBodyJs.put("notificationMessage", NOTIFICATION_MESSAGE)
            //where to send
            notificationJs.put("to", NOTIFICATION_TOPIC)//to all who subscribed this topic
            notificationJs.put("data", notificationBodyJs)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        sendFcmNotification(notificationJs, orderId)
    }

    private fun sendFcmNotification(notificationJs: JSONObject, orderId: String) {
        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            "https://fcm.googleapis.com/fcm/send",
            notificationJs,
            Response.Listener {
                //after sending fcm start order details activity
                val intent = Intent(this, NewActivity::class.java)
                startActivity(intent)

            },
            Response.ErrorListener {
                //if failed sending fcm,still start order details activity
                Toast.makeText(this, "Some error occured", Toast.LENGTH_SHORT).show()

            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "key=AAAA0TgW0AY:APA91bGNGMLtISkxVjfP-Mvu6GCZeeTcoDzvFtUg0Pq1SrJ9SshsFXDuXR9i3-lOqtlUjVmGqmv4C0sSRbsIphiacRau5c1ERQEUBukLxV-EXGVGv1ZmTN796LyLs1Wd7s1Tnu60e_2D"
                return headers
            }
        }
        Volley.newRequestQueue(this).add(jsonObjectRequest)



    }
}