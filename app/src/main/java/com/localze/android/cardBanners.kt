package com.localze.android

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
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
                        CardBanners()
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
                                            cardScroll.visibility = View.GONE
                                            rl_accessCard.visibility = View.VISIBLE
                                        }
                                        "Total Access" -> {
                                            CardBanners()
                                        }
                                        "Order Access" -> {
                                            cardScroll.visibility = View.GONE
                                            rl_accessCard.visibility = View.VISIBLE

                                        }
                                        "Delivery Access" -> {
                                            cardScroll.visibility = View.GONE
                                            rl_accessCard.visibility = View.VISIBLE

                                        }
                                        "Catalogue Access(Product)" -> {
                                            cardScroll.visibility = View.GONE
                                            rl_accessCard.visibility = View.VISIBLE

                                        }
                                        "Boost Your Shop Access" -> {
                                            carStaff.visibility = View.GONE
                                            CardBanners()
                                            carStaff.isClickable = false
                                        }
                                        "(Orders + Catalogue)Access" -> {
                                            cardScroll.visibility = View.GONE
                                            rl_accessCard.visibility = View.VISIBLE

                                        }
                                        "(Order + Boost Your Shop)Access" -> {
                                            carStaff.visibility = View.GONE
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

    private fun CardBanners() {
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
        cardShare.setOnClickListener {
            createLink()

        }
    }

    private fun createLink() {
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("http://www.localze.com/"))
            .setDynamicLinkDomain("https://localzes.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
            .buildDynamicLink()
        val dynamicLinkUri = dynamicLink.uri
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, dynamicLinkUri.toString())
        intent.type = "text/plain"
        startActivity(intent)
       /* val shortLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(dynamicLinkUri)
            .setDomainUriPrefix("https://localzes.page.link")
            .buildShortDynamicLink().addOnCompleteListener {
                if (it.isSuccessful) {
                    val shortLink = it.result?.shortLink

                }
            }*/
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}