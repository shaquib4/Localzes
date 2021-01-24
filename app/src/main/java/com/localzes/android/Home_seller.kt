package com.localzes.android

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home_seller.*
import util.ConnectionManager

class Home_seller : AppCompatActivity() {
    private lateinit var ordersAccepted: TextView
    private lateinit var ordersOutForDelivery: TextView
    private lateinit var ordersCompleted: TextView
    private lateinit var ordersPending: TextView
    private lateinit var orderAuth: FirebaseAuth
    private lateinit var orderDatabaseReference: DatabaseReference
    private lateinit var editShopDetails: ImageView
    private lateinit var shopName: TextView
    private lateinit var totalOrders: TextView
    private lateinit var totalIncome: TextView
    private lateinit var switchButton: Button
    private var t1: Int = 0
    private var t2: Int = 0
    private var t3: Int = 0
    private var t4: Int = 0
    private var t5: Int = 0
    private var t6: Int = 0
    private var t7: Int = 0
    private var t8: Int = 0
    private var t: Int = 0
    private var cartOrders: Int = 0
    private var listOrders: Int = 0
    private var cartIncome: Double = 0.0
    private var listIncome: Double = 0.0
    private var backPressedTime = 0L

    //private lateinit var storeViews:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_seller)
        editShopDetails = findViewById(R.id.editShopDetails)
        shopName = findViewById(R.id.txtShopName)
        totalOrders = findViewById(R.id.txtTO)
        totalIncome = findViewById(R.id.txtTI)
        // storeViews=findViewById(R.id.txtSV)
        ordersAccepted = findViewById(R.id.txtItemAccepted)
        ordersOutForDelivery = findViewById(R.id.txtItemOFD)
        ordersCompleted = findViewById(R.id.txtItemActive)
        ordersPending = findViewById(R.id.txtItemPending)
        switchButton = findViewById(R.id.switchButtonHomeSeller)
        orderAuth = FirebaseAuth.getInstance()
        val user = orderAuth.currentUser
        val uid = user!!.uid
        switchButton.setOnClickListener {
            val intent = Intent(this, Continueas::class.java)
            startActivity(intent)
            finish()
        }
        sellerRetry.setOnClickListener {
            this.recreate()
        }

        bottom_navHome_seller.selectedItemId = R.id.nav_store_seller
        bottom_navHome_seller.setOnNavigationItemSelectedListener { item ->


            when (item.itemId) {


                R.id.nav_product_seller -> {

                    startActivity(Intent(this, Seller_Products::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_order_seller -> {

                    startActivity(Intent(this, cardBanners::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_account_seller -> {

                    startActivity(Intent(this, AccountsSeller::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_store_seller -> {

                    return@setOnNavigationItemSelectedListener true

                }
                R.id.nav_category_seller -> {

                    startActivity(Intent(this, Category::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener false
        }
        orderDatabaseReference = FirebaseDatabase.getInstance().reference.child("seller")
        orderDatabaseReference.child(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!(snapshot.child("staffOfShop")
                            .exists()) || snapshot.child("staffOfShop").value.toString() == ""
                    ) {
                        homeSeller(uid)
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
                                        "Total Access" -> {
                                            homeSeller(uidOfShop)
                                        }
                                        "Order Access" -> {
                                            homeSeller(uidOfShop)
                                            editShopDetails.visibility = View.GONE
                                            totalIncome.visibility = View.GONE
                                        }
                                        "Delivery Access" -> {

                                            ordersPending.visibility = View.GONE
                                            editShopDetails.visibility = View.GONE
                                            totalIncome.visibility = View.GONE
                                            homeSeller(uidOfShop)
                                            orderPen.isClickable = false
                                        }
                                        "Catalogue Access(Product)" -> {
                                            rl_HomeSeller.visibility = View.GONE
                                            rl_accessHome.visibility = View.VISIBLE
                                        }
                                        "Boost Your Shop Access" -> {
                                            rl_HomeSeller.visibility = View.GONE
                                            rl_accessHome.visibility = View.VISIBLE
                                        }
                                        "(Orders + Catalogue)Access" -> {
                                            homeSeller(uidOfShop)
                                            editShopDetails.visibility = View.GONE
                                            totalIncome.visibility = View.GONE
                                        }
                                        "(Order + Boost Your Shop)Access" -> {
                                            homeSeller(uidOfShop)
                                            editShopDetails.visibility = View.GONE
                                            totalIncome.visibility = View.GONE
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
            })
    }

    private fun homeSeller(uid: String) {
        orderDatabaseReference.child(uid).child("Orders")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    cartOrders = snapshot.childrenCount.toInt()
                }

            })
        orderDatabaseReference.child(uid).child("OrdersLists")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    listOrders = snapshot.childrenCount.toInt()
                    totalOrders.text = (cartOrders + listOrders).toString()
                }

            })
        orderDatabaseReference.child(uid).child("Orders").orderByChild("orderStatus")
            .equalTo("Pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    t1 = snapshot.childrenCount.toInt()
                }
            })
        orderDatabaseReference.child(uid).child("OrdersLists").orderByChild("orderStatus")
            .equalTo("Pending")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    t2 = snapshot.childrenCount.toInt()
                    t = t1 + t2
                    ordersPending.text = t.toString()

                }
            })

        orderDatabaseReference.child(uid).child("Orders").orderByChild("orderStatus")
            .equalTo("Accepted")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    t3 = snapshot.childrenCount.toInt()
                }
            })
        orderDatabaseReference.child(uid).child("OrdersLists").orderByChild("orderStatus")
            .equalTo("Accepted")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    t4 = snapshot.childrenCount.toInt()
                    t = t3 + t4
                    ordersAccepted.text = t.toString()

                }

            })
        orderDatabaseReference.child(uid).child("Orders").orderByChild("orderStatus")
            .equalTo("Out For Delivery")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    t5 = snapshot.childrenCount.toInt()
                }

            })
        orderDatabaseReference.child(uid).child("OrdersLists").orderByChild("orderStatus")
            .equalTo("Out For Delivery").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    t6 = snapshot.childrenCount.toInt()
                    t = t5 + t6
                    ordersOutForDelivery.text = t.toString()
                }

            })
        orderDatabaseReference.child(uid).child("Orders").orderByChild("orderStatus")
            .equalTo("Completed")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    t7 = snapshot.childrenCount.toInt()

                }

            })
        orderDatabaseReference.child(uid).child("OrdersLists").orderByChild("orderStatus")
            .equalTo("Completed")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    t8 = snapshot.childrenCount.toInt()
                    t = t7 + t8
                    ordersCompleted.text = t.toString()

                }
            })
        orderDatabaseReference.child(uid).child("OrdersLists")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var finalPriceOfList = arrayListOf<Double>()
                    for (i in snapshot.children) {
                        finalPriceOfList.clear()
                        val cost = i.child("orderCost").value.toString()
                        val status = i.child("orderStatus").value.toString()
                        if (status == "Completed") {
                            finalPriceOfList.add(cost.toDouble())
                        }
                        for (j in finalPriceOfList) {
                            listIncome += j
                        }

                    }

                }
            })

        orderDatabaseReference.child(uid).child("Orders")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var finalPriceOfCart = arrayListOf<Double>()
                    for (i in snapshot.children) {
                        finalPriceOfCart.clear()
                        val cost = i.child("orderCost").value.toString()
                        val status = i.child("orderStatus").value.toString()
                        if (status == "Completed") {
                            finalPriceOfCart.add(cost.toDouble())
                        }
                        for (k in finalPriceOfCart) {
                            cartIncome += k
                        }
                        val l = cartIncome + listIncome
                        totalIncome.text = l.toString()
                    }

                }


            })

        OrderAcc.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_HomeSeller.visibility = View.VISIBLE
                rl_Seller_Internet.visibility = View.GONE
                val intent = Intent(this, OrdersAcceptedActivity::class.java)
                intent.putExtra("UID", uid)
                startActivity(intent)
                finish()
            } else {
                rl_HomeSeller.visibility = View.GONE
                rl_Seller_Internet.visibility = View.VISIBLE
            }
        }
        orderCom.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_HomeSeller.visibility = View.VISIBLE
                rl_Seller_Internet.visibility = View.GONE
                val intent = Intent(this, OrdersCompletedActivity::class.java)
                intent.putExtra("UID", uid)
                startActivity(intent)
                finish()
            } else {
                rl_HomeSeller.visibility = View.GONE
                rl_Seller_Internet.visibility = View.VISIBLE
            }
        }
        orderPen.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_HomeSeller.visibility = View.VISIBLE
                rl_Seller_Internet.visibility = View.GONE
                val intent = Intent(this, SellerOrdersActivity::class.java)
                intent.putExtra("UID", uid)
                startActivity(intent)
                finish()
            } else {
                rl_HomeSeller.visibility = View.GONE
                rl_Seller_Internet.visibility = View.VISIBLE
            }
        }
        orderOFD.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_HomeSeller.visibility = View.VISIBLE
                rl_Seller_Internet.visibility = View.GONE
                val intent = Intent(this, OrderOutForDeliveryActivity::class.java)
                intent.putExtra("UID", uid)
                startActivity(intent)
                finish()
            } else {
                rl_HomeSeller.visibility = View.GONE
                rl_Seller_Internet.visibility = View.VISIBLE
            }
        }
        editShopDetails.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_HomeSeller.visibility = View.VISIBLE
                rl_Seller_Internet.visibility = View.GONE
                val intent = Intent(this, UpdateShopDetailActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                rl_HomeSeller.visibility = View.GONE
                rl_Seller_Internet.visibility = View.VISIBLE
            }
        }
        orderDatabaseReference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val shop = snapshot.child("shop_name").value.toString()
                shopName.text = shop
            }
        })
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(
                applicationContext,
                "Press back again to exit the app",
                Toast.LENGTH_SHORT
            ).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}