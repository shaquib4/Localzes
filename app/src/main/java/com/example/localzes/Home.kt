package com.example.localzes

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterUserShops
import com.example.localzes.Modals.Upload
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
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
    private lateinit var shopc: List<Upload>
    private var shopId: String? = null
    private lateinit var recyclerShopUser: RecyclerView
    private lateinit var userShopAdapter: AdapterUserShops
    private lateinit var relativeHome: RelativeLayout
    private lateinit var categoryAll: ImageView
    private lateinit var categoryGrocery: ImageView
    private lateinit var categoryFood: ImageView
    private lateinit var categoryElectronics: ImageView
    private lateinit var categoryFashion: ImageView
    private lateinit var categoryMobiles: ImageView
    private lateinit var categoryHome: ImageView
    private lateinit var categoryBeauty: ImageView
    private lateinit var categoryAppliances: ImageView
    private lateinit var categoryToys: ImageView
    private lateinit var categorySports: ImageView
    private lateinit var categoryBooks: ImageView
    private lateinit var categoryAutomobiles: ImageView
    private lateinit var categoryFitness: ImageView
    private lateinit var categoryHealth: ImageView
    private lateinit var categoryTravel: ImageView
    private lateinit var categoryGaming: ImageView
    private lateinit var categoryOthers: ImageView
    private lateinit var switchButton: Button
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
        categoryGrocery = findViewById(R.id.imgGrocery)
        categoryFood = findViewById(R.id.imgFood)
        categoryElectronics = findViewById(R.id.imgElectronics)
        categoryFashion = findViewById(R.id.imgFashion)
        categoryMobiles = findViewById(R.id.imgMobiles)
        categoryHome = findViewById(R.id.imgHome)
        categoryBeauty = findViewById(R.id.imgBeauty)
        categoryAppliances = findViewById(R.id.imgAppliances)
        categoryToys = findViewById(R.id.imgToys)
        categorySports = findViewById(R.id.imgSports)
        categoryBooks = findViewById(R.id.imgBooks)
        categoryAutomobiles = findViewById(R.id.imgAutomobiles)
        categoryFitness = findViewById(R.id.imgFitness)
        categoryHealth = findViewById(R.id.imgHealth)
        categoryTravel = findViewById(R.id.imgTravel)
        categoryGaming = findViewById(R.id.imgGaming)
        categoryAll = findViewById(R.id.imgAll)
        categoryOthers = findViewById(R.id.imgOthers)
        switchButton = findViewById(R.id.switchButtonHome)
        switchButton.setOnClickListener {
            val intent = Intent(this, Continueas::class.java)
            startActivity(intent)
            finish()
        }
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
        shopc = ArrayList<Upload>()
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
        categoryGrocery.setOnClickListener {
            loadShops("Grocery")
        }
        categoryFood.setOnClickListener {
            loadShops("Food")
        }
        categoryElectronics.setOnClickListener {
            loadShops("Electronics")
        }
        categoryMobiles.setOnClickListener {
            loadShops("Mobiles")
        }
        categoryBeauty.setOnClickListener {
            loadShops("Beauty")
        }
        categoryFashion.setOnClickListener {
            loadShops("Fashion")
        }
        categoryBooks.setOnClickListener {
            loadShops("Books")
        }
        categoryHealth.setOnClickListener {
            loadShops("Medical")
        }
        categoryAutomobiles.setOnClickListener {
            loadShops("Automobiles")
        }
        categoryTravel.setOnClickListener {
            loadShops("Travel")
        }
        categorySports.setOnClickListener {
            loadShops("Sports")
        }
        categoryFitness.setOnClickListener {
            loadShops("Fitness")
        }
        categoryHealth.setOnClickListener {
            loadShops("Health")

        }
        categoryTravel.setOnClickListener {
            loadShops("Travel")
        }
        categoryGaming.setOnClickListener {
            loadShops("Gaming")
        }
        categoryOthers.setOnClickListener {
            loadShops("Others")
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
                            i.child("closingDay").value.toString(),
                            i.child("locality2").value.toString()
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
                            i.child("closingDay").value.toString(),
                            i.child("locality2").value.toString()
                        )
                        if (currentCity.toLowerCase() == i.child("city").value.toString()) {


                            (shops as ArrayList<Upload>).add(obj)
                            progress_home.visibility = View.GONE

                        } else {
                            progress_home.visibility = View.GONE
                        }
                    }
                    (shopc as ArrayList<Upload>).clear()
                    for (i in shops) {
                        val shopId = i.shopId
                        val phone = i.phone
                        val name = i.name
                        val email = i.email
                        val address = i.address
                        val shopName = i.shop_name
                        val obj = Upload(
                            i.shopId,
                            i.phone,
                            i.name,
                            i.email,
                            i.address,
                            i.shop_name,
                            i.imageUrl,
                            i.category1,
                            i.upi,
                            i.locality,
                            i.city,
                            i.pinCode,
                            i.state,
                            i.country,
                            i.openingTime,
                            i.closingTime,
                            i.closingDay,
                            i.locality2
                        )
                        userDatabase.child(shopId).child("Categories")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {

                                }

                                override fun onDataChange(snapshot: DataSnapshot) {

                                    for (i in snapshot.children) {
                                        val cat = i.child("category").value.toString()
                                        if (proCat == cat) {
                                            (shopc as ArrayList<Upload>).add(obj)

                                        }
                                    }
                                    if (shops.isEmpty()) {
                                        recyclerShopUser.visibility = View.GONE
                                    } else {
                                        relativeHome.visibility = View.GONE
                                        recyclerShopUser.visibility = View.VISIBLE
                                        userShopAdapter = AdapterUserShops(
                                            this@Home,
                                            shopc
                                        )
                                        recyclerShopUser.adapter = userShopAdapter

                                    }
                                }
                            })
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