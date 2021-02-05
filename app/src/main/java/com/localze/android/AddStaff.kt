package com.localze.android

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject
import java.net.URLEncoder

class AddStaff : AppCompatActivity() {
    private lateinit var staffNumber: EditText
    private lateinit var accessStaff: Button
    private lateinit var btnConfirmStaff: Button
    private var bool: Boolean = false
    private var phoneCheck: String? = null
    private var staffName: String? = null
    private var address: String? = null
    private var staffuid: String? = null
    private lateinit var progressDialog: ProgressDialog
    private lateinit var shopAuth: FirebaseAuth
    var selectedAccess: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_staff)
        staffNumber = findViewById(R.id.edtStaffNumber)
        accessStaff = findViewById(R.id.Access_staff)
        btnConfirmStaff = findViewById(R.id.btnConfirmStaff)
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val sellerUid = user!!.uid
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        /*sendMessage.setOnClickListener {
            try {
                val mobile = "917317544896"
                val msg = "Its Working"
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://api.whatsapp.com/send?phone=$mobile&text=$msg")
                    )
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Please install whatsapp", Toast.LENGTH_SHORT).show()
            }

        }*/
        /*  staffNumber.addTextChangedListener(object:TextWatcher{
              override fun afterTextChanged(s: Editable?) {

              }

              override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

              }

              override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                  Toast.makeText(this@AddStaff,s?.length.toString(),Toast.LENGTH_SHORT).show()
                  staff(s?.length.toString())
              }
          })*/

        accessStaff.setOnClickListener {
            val options = arrayOf(
                "No Access",
                "Total Access",
                "Order Access",
                "Delivery Access",
                "Catalogue Access(Product)",
                "Boost Your Shop Access",
                "(Orders + Catalogue)Access",
                "(Order + Boost Your Shop)Access"
            )
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Access Modes")
            builder.setSingleChoiceItems(options, -1) { dialog, which ->
                val selectedItem = options[which]
                selectedAccess = selectedItem
                accessStaff.text = selectedAccess
                dialog.dismiss()
            }
            builder.create().show()
        }
        btnConfirmStaff.setOnClickListener {

            progressDialog.setMessage("Fetching details.....")
            progressDialog.show()
            val dataRef = FirebaseDatabase.getInstance().reference.child("seller")
            dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        val phone = i.child("phone").value.toString()
                        if (staffNumber.text.toString() == phone) {
                            phoneCheck = phone
                            staffName = i.child("name").value.toString()
                            address = i.child("address").value.toString()
                            staffuid = i.child("shopId").value.toString()

                        }
                    }
                    if (staffNumber.text.toString() == phoneCheck && phoneCheck.toString()
                            .isNotEmpty()
                    ) {
                        if (snapshot.child(sellerUid).child("MyStaff").child(staffuid.toString())
                                .exists()
                        ) {
                            Toast.makeText(this@AddStaff, "Staff Already exists", Toast.LENGTH_LONG)
                                .show()
                            progressDialog.dismiss()
                        } else {

                            val headers = HashMap<String, Any>()
                            headers["name"] = staffName.toString()
                            headers["phone"] = phoneCheck.toString()
                            headers["address"] = address.toString()
                            headers["status"] = ""
                            headers["access"] = selectedAccess
                            headers["uid"] = staffuid.toString()
                            headers["invitationStatus"] = ""
                            dataRef.child(sellerUid).child("MyStaff").child(staffuid.toString())
                                .updateChildren(headers)
                                .addOnSuccessListener {
                                    val newHeader = HashMap<String, Any>()
                                    newHeader["shopOwnerName"] =
                                        snapshot.child(sellerUid).child("name").value.toString()
                                    newHeader["shopName"] = snapshot.child(sellerUid)
                                        .child("shop_name").value.toString()
                                    newHeader["shopMobileNumber"] =
                                        snapshot.child(sellerUid).child("phone").value.toString()
                                    newHeader["status"] = ""
                                    newHeader["invitationUid"] = sellerUid
                                    newHeader["invitationStatus"] = ""
                                    val staffHeader = HashMap<String, Any>()
                                    staffHeader["staffOfShop"] = ""
                                    dataRef.child(staffuid.toString()).updateChildren(staffHeader)
                                    dataRef.child(staffuid.toString()).child("StaffOf")
                                        .child(sellerUid)
                                        .updateChildren(newHeader).addOnSuccessListener {
                                            prepareNotificationMessage(
                                                snapshot.child(sellerUid)
                                                    .child("shop_name").value.toString()
                                            )
                                            progressDialog.dismiss()
                                            Toast.makeText(
                                                this@AddStaff,
                                                "New Staff is added successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                        }
                    } else {
                        progressDialog.dismiss()
                        val builder = AlertDialog.Builder(this@AddStaff)
                        val view =
                            LayoutInflater.from(this@AddStaff).inflate(R.layout.custom_layout, null)
                        builder.setView(view)
                        val show = builder.show()
                        val shareLink = view.findViewById<Button>(R.id.shareLink)
                        val cancel = view.findViewById<Button>(R.id.Cancel)
                        shareLink.setOnClickListener {
                            show.dismiss()
                            try {
                                val mobile = staffNumber.text.toString()
                                val msg =
                                    "Cupcakes want you to be registered as their staff.To download the app,click" + URLEncoder.encode(
                                        "https://play.google.com/store/apps/details?id=com.localze.android",
                                        "utf-8"
                                    ) + "and continue as Seller to accept invitation"
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://api.whatsapp.com/send?phone=$mobile&text=$msg")
                                    )
                                )

                            } catch (e: Exception) {

                            }
                            cancel.setOnClickListener {
                                show.dismiss()
                            }
                        }
                    }
                }
            })
        }

    }

    private fun prepareNotificationMessage(shopName: String) {
        val NOTIFICATION_TOPIC = "/topics/PUSH_NOTIFICATIONS"
        val NOTIFICATION_TITLE = "Pending Invitation"
        val NOTIFICATION_MESSAGE = "You have a new invitation from $shopName as a staff"
        val NOTIFICATION_TYPE = "PendingInvitation"
        val notificationJs = JSONObject()
        val notificationBodyJs = JSONObject()
        try {
            notificationBodyJs.put("notificationType", NOTIFICATION_TYPE)
            notificationBodyJs.put("senderId", (shopAuth.currentUser)!!.uid)
            notificationBodyJs.put("receiverId", staffuid.toString())
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
        val jsonObjectRequest = object : JsonObjectRequest(
            "https://fcm.googleapis.com/fcm/send",
            notificationJs,
            Response.Listener {
                //notification sent
            },
            Response.ErrorListener {
                //notification failed
                Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()

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

    private fun checkuid() {
        val dataRef = FirebaseDatabase.getInstance().reference.child("seller").child("MyStaff")
    }

    private fun confirm() {

    }

    private fun staff(toString: String) {
        for (i in toString()) {
            if (toString == 10.toString()) {
                progressDialog.setMessage("Fetching details.....")
                progressDialog.show()
                val ref = FirebaseDatabase.getInstance().reference.child("seller")
                ref.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children) {
                            val phone = i.child("phone").value.toString()
                            if (staffNumber.text.toString() == phone) {
                                bool = true
                            }
                        }
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MyStaffActivity::class.java)
        startActivity(intent)
        finish()
    }
}
