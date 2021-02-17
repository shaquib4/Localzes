package com.localze.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_address_seller_edit.*
import kotlinx.android.synthetic.main.address_single_row.*

class AddressSellerEdit : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var ad:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_seller_edit)
        auth=FirebaseAuth.getInstance()
        val user=auth.currentUser
        val uid=user!!.uid
        val cardDatabaseRef =
            FirebaseDatabase.getInstance().reference.child("seller")
        cardDatabaseRef.child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    if (!(snapshot.child("staffOfShop")
                            .exists()) || snapshot.child("staffOfShop").value.toString() == ""
                    ) {
                        val address=snapshot.child("address").value.toString()
                        txtAddressSeller.text=address
                        ad=address
                       txtEditAd.visibility=View.VISIBLE
                        txtEditAd.setOnClickListener {
                            startActivity(Intent(this@AddressSellerEdit,MapsActivitySellerUpdate::class.java))
                            finish()
                        }
                    } else {
                        val uidOfShop = snapshot.child("staffOfShop").value.toString()
                        if (snapshot.child("StaffOf").child(uidOfShop).exists()) {
                            val databaseReference =
                                FirebaseDatabase.getInstance().reference.child("seller")
                                    .child(uidOfShop).child("MyStaff").child(uid)
                            databaseReference.addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val access = snapshot.child("access").value.toString()
                                    when (access) {
                                        "No Access" -> {
                                            txtAddress.text=ad.toString()
                                            txtEditAd.visibility=View.INVISIBLE
                                        }
                                        "Total Access" -> {
                                            txtAddress.text=ad.toString()
                                            txtEditAd.visibility=View.VISIBLE
                                            txtEditAd.setOnClickListener {
                                                startActivity(Intent(this@AddressSellerEdit,MapsActivitySellerUpdate::class.java))
                                                finish()
                                            }
                                        }
                                        "Order Access" -> {
                                            txtAddress.text=ad.toString()
                                            txtEditAd.visibility=View.INVISIBLE

                                        }
                                        "Delivery Access" -> {
                                            txtAddress.text=ad.toString()
                                            txtEditAd.visibility=View.INVISIBLE

                                        }
                                        "Catalogue Access(Product)" -> {
                                            txtAddress.text=ad.toString()
                                            txtEditAd.visibility=View.INVISIBLE

                                        }
                                        "Boost Your Shop Access" -> {
                                            txtAddress.text=ad.toString()
                                            txtEditAd.visibility=View.INVISIBLE
                                        }
                                        "(Orders + Catalogue)Access" -> {
                                            txtAddress.text=ad.toString()
                                            txtEditAd.visibility=View.INVISIBLE

                                        }
                                        "(Order + Boost Your Shop)Access" -> {
                                            txtAddress.text=ad.toString()
                                            txtEditAd.visibility=View.INVISIBLE
                                        }
                                    }
                                }

                            })
                        }
                    }

                }

            })
    }
}