package com.localze.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BankDetailsActivity : AppCompatActivity() {
    private lateinit var accNumber: TextView
    private lateinit var beneficiaryName: TextView
    private lateinit var accountType: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var accountLayout: RelativeLayout
    private lateinit var noAccountLayout: RelativeLayout
    private lateinit var addAccount: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_details)
        accNumber = findViewById(R.id.acc_number)
        beneficiaryName = findViewById(R.id.beneficiary_Name)
        accountType = findViewById(R.id.acc_type)
        accountLayout = findViewById(R.id.account)
        noAccountLayout = findViewById(R.id.noAccount)
        addAccount = findViewById(R.id.btnShopNow)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Account_Details")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        FirebaseDatabase.getInstance().reference.child("customers").child(uid)
                            .child("AccountDetails")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val accountNo = snapshot.child("Account_no").value.toString()
                                    val benName =
                                        snapshot.child("Beneficiary_name").value.toString()
                                    val accType = snapshot.child("Account_type").value.toString()
                                    accNumber.text = "XXXXXXXX" + accountNo.takeLast(4)
                                    beneficiaryName.text = benName
                                    accountType.text = accType
                                }
                            })

                    } else {
                        accountLayout.visibility = View.INVISIBLE
                        noAccountLayout.visibility = View.VISIBLE
                        addAccount.setOnClickListener {
                            val intent =
                                Intent(this@BankDetailsActivity, AccountDetails::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            })
    }
}