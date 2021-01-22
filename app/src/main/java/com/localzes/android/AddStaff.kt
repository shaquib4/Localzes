package com.localzes.android

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddStaff : AppCompatActivity() {
    private lateinit var staffNumber: EditText
    private lateinit var accessStaff: Button
    private lateinit var btnConfirmStaff: Button
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
        accessStaff.setOnClickListener {
            val options = arrayOf(
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
            dataRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        val phone = i.child("phone").value.toString()
                        if (staffNumber.text.toString() == phone) {
                            val uid = i.child("shopId").value.toString()
                            val headers = HashMap<String, Any>()
                            headers["name"] = i.child("name").value.toString()
                            headers["phone"] = i.child("phone").value.toString()
                            headers["address"] = i.child("address").value.toString()
                            headers["status"] = ""
                            headers["access"] = selectedAccess
                            headers["uid"] = uid
                            dataRef.child(sellerUid).child("MyStaff").child(uid).setValue(headers)
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
                                    dataRef.child(uid).updateChildren(staffHeader)
                                    dataRef.child(uid).child("StaffOf").child(sellerUid)
                                        .updateChildren(newHeader).addOnSuccessListener {
                                            progressDialog.dismiss()
                                            Toast.makeText(
                                                this@AddStaff,
                                                "New Staff is added successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                        }
                    }
                }

            })
        }

    }
}