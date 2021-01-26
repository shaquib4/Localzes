package com.localzes.android

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
                        if(snapshot.child(sellerUid).child("MyStaff").child(staffuid.toString()).exists()){
                           Toast.makeText(this@AddStaff,"Staff Already exists",Toast.LENGTH_LONG).show()
                            progressDialog.dismiss()
                        }else{

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
                                dataRef.child(staffuid.toString()).child("StaffOf").child(sellerUid)
                                    .updateChildren(newHeader).addOnSuccessListener {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            this@AddStaff,
                                            "New Staff is added successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }}
                    }else{
                        Toast.makeText(this@AddStaff,"value",Toast.LENGTH_LONG).show()
                        progressDialog.dismiss()
                    }
                }

            })
        }

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
