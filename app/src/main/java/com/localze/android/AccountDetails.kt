package com.localze.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_account_details.*

class AccountDetails : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference
        addDetails.setOnClickListener {
            when {
                edtAccountNumber.text.isEmpty() -> {
                    edtAccountNumber.error = "Please Enter Account Number"
                }
                edtBeneficiaryName.text.isEmpty() -> {
                    edtBeneficiaryName.error = "Please Enter Name"
                }
                edtIFSC.text.isEmpty() -> {
                    edtIFSC.error = "Please Enter IFSC Code"
                }
                else -> {
                    val headers = HashMap<String, Any>()
                    val header = HashMap<String, Any>()
                    headers["Account_no"] = edtAccountNumber.text.toString()
                    headers["Beneficiary_name"] = edtBeneficiaryName.text.toString()
                    headers["Account_type"] = spn_type.selectedItem.toString()
                    headers["IFSC"] = edtIFSC.text.toString()
                    header["Account_Details"] = "true"

                    databaseReference.child("seller").child(uid).updateChildren(header)
                    databaseReference.child("customers").child(uid).child("AccountDetails")
                        .updateChildren(headers).addOnSuccessListener {
                            val snackbar = Snackbar.make(
                                accDetail,
                                "Account Added Successfully",
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.show()
                        }
                    val intent=Intent(this,Home_seller::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }


    }
}