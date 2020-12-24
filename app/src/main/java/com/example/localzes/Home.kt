package com.example.localzes

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterUserShops
import com.example.localzes.Modals.Upload
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_home.*
import util.ConnectionManager

class Home : AppCompatActivity() {
    private lateinit var userDatabase: DatabaseReference
    private lateinit var mUserDatabase: DatabaseReference
    var firebaseUser: FirebaseUser? = null
    private lateinit var shops: List<Upload>
    private var shopId: String? = null
    private lateinit var recyclerShopUser: RecyclerView
    private lateinit var userShopAdapter: AdapterUserShops
    private lateinit var relativeHome: RelativeLayout
    private lateinit var category1: CardView
    private lateinit var category2: CardView
    private lateinit var category3: CardView
    private lateinit var category4: CardView
    private lateinit var category5: CardView
    private lateinit var category6: CardView
    private lateinit var category7: CardView
    private lateinit var category8: CardView
    private lateinit var category9: CardView
    private lateinit var categoryAll: CardView
    private var currentCity: String = ""
    private var cate: String = ""
    private var backPressedTime = 0L
    private var bool = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        progress_home.visibility = View.VISIBLE
        recyclerShopUser = findViewById(R.id.recycler_shop_user)
        relativeHome = findViewById(R.id.rl_Home)
        category1 = findViewById(R.id.cardGrocery)
        category2 = findViewById(R.id.cardFood1)
        category3 = findViewById(R.id.cardElectronics2)
        category4 = findViewById(R.id.cardMobiles4)
        category5 = findViewById(R.id.cardHome5)
        category6 = findViewById(R.id.cardBeauty6)
        category7 = findViewById(R.id.cardAppliances7)
        category8 = findViewById(R.id.cardToys8)
        category9 = findViewById(R.id.cardSports8)
        categoryAll = findViewById(R.id.cardAll)
        retry.setOnClickListener {
            this.recreate()
        }

        recyclerShopUser.layoutManager = LinearLayoutManager(this)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        userDatabase = FirebaseDatabase.getInstance().reference.child("seller")
        mUserDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
                .child("current_address")
        shops = ArrayList<Upload>()
        mUserDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                currentCity = snapshot.child("city").value.toString()
            }

        })
        categoryAll.setOnClickListener {
            loadAllShops()
        }
        category1.setOnClickListener {
            loadShops("Groceries")
        }
        category2.setOnClickListener {
            loadShops("Food")

        }
        category3.setOnClickListener {
            loadShops("Electronics")
        }
        category4.setOnClickListener {
            loadShops("Mobiles")
        }
        category5.setOnClickListener {
            loadShops("Electronics")
        }
        category6.setOnClickListener {
            loadShops("Fashion")
        }
        category7.setOnClickListener {
            loadShops("Digital Shop")
        }
        category8.setOnClickListener {
            loadShops("Medical")
        }
        category9.setOnClickListener {
            loadShops("Automobiles")
        }
        imgScan.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            scanner.setBeepEnabled(false)
            scanner.initiateScan()
        }
        //loadAllShops()
        bottom_navProducts.selectedItemId = R.id.nav_home
        bottom_navProducts.setOnNavigationItemSelectedListener { item ->


            when (item.itemId) {

                R.id.nav_home -> {

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_search -> {

                    startActivity(Intent(this, Search::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_cart -> {

                    startActivity(Intent(this, Cart::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_account -> {

                    startActivity(Intent(this, Accounts::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val shop = i.child("shopId").value.toString()

                    userDatabase.child(shop).child("Categories")
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (i in snapshot.children) {
                                    val cat = i.child("category").value.toString()
                                    cate = cat
                                }
                            }
                        })
                }

            }
        })

    }


    private fun loadAllShops() {
        if (ConnectionManager().checkConnectivity(this)) {
            userDatabase.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (shops as ArrayList<Upload>).clear()
                    for (i in snapshot.children) {
                        rl.visibility = View.VISIBLE
                        rl_retry.visibility = View.GONE

                        val obj = Upload(
                            i.child("shopId").value.toString(),
                            i.child("phone").value.toString(),
                            i.child("name").value.toString(),
                            i.child("email").value.toString(),
                            i.child("address").value.toString(),
                            i.child("shop_name").value.toString(),
                            i.child("imageUrl").value.toString(),
                            i.child("category1").value.toString(),
                            i.child("upi").value.toString(),
                            i.child("locality").value.toString(),
                            i.child("city").value.toString(),
                            i.child("pinCode").value.toString(),
                            i.child("state").value.toString(),
                            i.child("country").value.toString(),
                            i.child("openingTime").value.toString(),
                            i.child("closingTime").value.toString(),
                            i.child("closingDay").value.toString()
                        )
                        if (currentCity.toLowerCase() == i.child("city").value.toString()) {
                            (shops as ArrayList<Upload>).add(obj)
                            progress_home.visibility = View.GONE
                        } else {
                            progress_home.visibility = View.GONE
                        }
                    }
                    if (shops.isEmpty()) {
                        recyclerShopUser.visibility = View.GONE
                    } else {
                        relativeHome.visibility = View.GONE
                        recyclerShopUser.visibility = View.VISIBLE
                        userShopAdapter = AdapterUserShops(
                            this@Home,
                            shops
                        )
                        recyclerShopUser.adapter = userShopAdapter
                    }

                }

            })
        } else {
            rl.visibility = View.GONE
            rl_retry.visibility = View.VISIBLE
        }
    }

    private fun loadShops(proCat: String) {
        if (ConnectionManager().checkConnectivity(this)) {
            rl.visibility = View.VISIBLE
            rl_retry.visibility = View.GONE
            userDatabase.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    (shops as ArrayList<Upload>).clear()
                    for (i in snapshot.children) {

                        val obj = Upload(
                            i.child("shopId").value.toString(),
                            i.child("phone").value.toString(),
                            i.child("name").value.toString(),
                            i.child("email").value.toString(),
                            i.child("address").value.toString(),
                            i.child("shop_name").value.toString(),
                            i.child("imageUrl").value.toString(),
                            i.child("category1").value.toString(),
                            i.child("upi").value.toString(),
                            i.child("locality").value.toString(),
                            i.child("city").value.toString(),
                            i.child("pinCode").value.toString(),
                            i.child("state").value.toString(),
                            i.child("country").value.toString(),
                            i.child("openingTime").value.toString(),
                            i.child("closingTime").value.toString(),
                            i.child("closingDay").value.toString()
                        )
                        if (currentCity.toLowerCase() == i.child("city").value.toString() && cate == proCat.toString()) {

                            (shops as ArrayList<Upload>).add(obj)
                            progress_home.visibility = View.GONE

                        } else {
                            progress_home.visibility = View.GONE
                        }
                    }
                    if (shops.isEmpty()) {
                        recyclerShopUser.visibility = View.GONE
                    } else {
                        relativeHome.visibility = View.GONE
                        recyclerShopUser.visibility = View.VISIBLE
                        userShopAdapter = AdapterUserShops(
                            this@Home,
                            shops
                        )
                        recyclerShopUser.adapter = userShopAdapter

                    }

                }

            })
        } else {
            rl.visibility = View.GONE
            rl_retry.visibility = View.VISIBLE
        }

    }

    private fun categoryExist(s: String, toString: String): Boolean {
        userDatabase.child(toString).child("Categories")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        if (i.child("category").value.toString() == s) {
                            bool = true

                        }
                    }
                }
            })
        return bool
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
                } else {
                    val shopId = result.contents
                    if (shopId.isNotEmpty()) {
                        val intent = Intent(this, UserProductsActivity::class.java)
                        intent.putExtra("shopId", shopId)
                        startActivity(intent)
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        loadAllShops()
    }
}