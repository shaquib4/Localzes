package com.localzes.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_card_banners.*

class cardBanners : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_banners)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        bottom_navOrders_seller.selectedItemId = R.id.nav_order_seller
        bottom_navOrders_seller.setOnNavigationItemSelectedListener {
            when (it.itemId) {


                R.id.nav_product_seller -> {

                    startActivity(Intent(this, Seller_Products::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_order_seller -> {

                    return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_account_seller -> {

                    startActivity(Intent(this, AccountsSeller::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_store_seller -> {

                    startActivity(Intent(this, Home_seller::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_category_seller -> {

                    startActivity(Intent(this, Category::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }

            return@setOnNavigationItemSelectedListener false
        }
      val  cardDatabaseRef =
            FirebaseDatabase.getInstance().reference.child("seller")
        cardDatabaseRef.child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!(snapshot.child("staffOfShop")
                            .exists()) || snapshot.child("staffOfShop").value.toString() == ""
                    ) {
                        CardBanners()
                    } else {
                        val uidOfShop = snapshot.value.toString()
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
                                        "Total Access" -> {
                                            CardBanners()
                                        }
                                        "Order Access" -> {
                                            rl_accessCard.visibility= View.GONE
                                            cardScroll.visibility=View.VISIBLE
                                        }
                                        "Delivery Access" -> {
                                            rl_accessCard.visibility= View.GONE
                                            cardScroll.visibility=View.VISIBLE
                                        }
                                        "Catalogue Access(Product)" -> {
                                            rl_accessCard.visibility= View.GONE
                                            cardScroll.visibility=View.VISIBLE
                                        }
                                        "Boost Your Shop Access" -> {
                                            carStaff.visibility=View.GONE
                                            CardBanners()
                                        }
                                        "(Orders + Catalogue)Access" -> {
                                            rl_accessCard.visibility= View.GONE
                                            cardScroll.visibility=View.VISIBLE
                                        }
                                        "(Order + Boost Your Shop)Access" -> {
                                            carStaff.visibility=View.GONE
                                            CardBanners()
                                        }
                                    }
                                }

                            })
                        }
                    }

                }

            })

    }
    private fun CardBanners(){
        cardQR.setOnClickListener {
            startActivity(Intent(this, generateQRcode::class.java))
            finish()
        }
        cardBanner.setOnClickListener {
            startActivity(Intent(this, ShopBanner::class.java))
            finish()
        }
        carStaff.setOnClickListener {
            startActivity(Intent(this, MyStaffActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}