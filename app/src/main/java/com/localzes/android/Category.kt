package com.localzes.android

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.localzes.android.Adapters.AdapterCategory
import com.localzes.android.Modals.ModelCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_seller__products.*
import util.ConnectionManager

class Category : AppCompatActivity() {
    private lateinit var btnAddNewCategory: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var shopAuth: FirebaseAuth
    private lateinit var recyclerCategories: RecyclerView
    private var timestamp: String = ""
    private lateinit var adapterCategory: AdapterCategory
    private lateinit var categories: List<ModelCategory>
    private var bool: Boolean = false
    private lateinit var categoryAccess: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        timestamp = System.currentTimeMillis().toString()
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory)
        recyclerCategories = findViewById(R.id.recycler_Category)
        categoryAccess = findViewById(R.id.rlCategoryAccess)
        categories = ArrayList<ModelCategory>()
        recyclerCategories.layoutManager = GridLayoutManager(this, 2)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child("seller")
        databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (!(snapshot.child("staffOfShop")
                        .exists()) || snapshot.child("staffOfShop").value.toString() == "Accepted"
                ) {
                    categorySeller(uid)
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
                                        categorySeller(uidOfShop)
                                    }
                                    "Order Access" -> {
                                        rl_category.visibility = View.GONE
                                        btnAddNewCategory.visibility=View.GONE
                                        categoryAccess.visibility = View.VISIBLE
                                    }
                                    "Delivery Access" -> {
                                        rl_category.visibility = View.GONE
                                        btnAddNewCategory.visibility=View.GONE
                                        categoryAccess.visibility = View.VISIBLE
                                    }
                                    "Catalogue Access(Product)" -> {
                                        categorySeller(uidOfShop)
                                    }
                                    "Boost Your Shop Access" -> {
                                        rl_category.visibility = View.GONE
                                        btnAddNewCategory.visibility=View.GONE
                                        categoryAccess.visibility = View.VISIBLE
                                    }
                                    "(Orders + Catalogue)Access" -> {
                                        categorySeller(uidOfShop)

                                    }
                                    "(Order + Boost Your Shop)Access" -> {
                                        rl_category.visibility = View.GONE
                                        btnAddNewCategory.visibility=View.GONE
                                        categoryAccess.visibility = View.VISIBLE
                                    }
                                }
                            }

                        })
                    }
                }
            }

        })


        categoryRetry.setOnClickListener {
            this.recreate()
        }

        bottom_navCategory_seller.selectedItemId = R.id.nav_category_seller
        bottom_navCategory_seller.setOnNavigationItemSelectedListener {
            when (it.itemId) {


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

                    startActivity(Intent(this, Home_seller::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
                R.id.nav_category_seller -> {

                    return@setOnNavigationItemSelectedListener true

                }
            }

            return@setOnNavigationItemSelectedListener false
        }

    }

    private fun categorySeller(uid: String) {
        btnAddNewCategory.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                rl_category.visibility = View.VISIBLE
                rl_retryCategory.visibility = View.GONE
                val builder = AlertDialog.Builder(this)
                val new = builder.create()
                val view: View =
                    LayoutInflater.from(this).inflate(R.layout.dialog_spinner, null, false)
                builder.setTitle("Choose a new Category")
                val mSpinner = view.findViewById<Spinner>(R.id.choose_new_category) as Spinner
                val adapter = ArrayAdapter<String>(
                    this@Category,
                    android.R.layout.simple_spinner_item,
                    resources.getStringArray(R.array.Spinner_Category)
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                mSpinner.adapter = adapter
                builder.setPositiveButton("Ok") { text, listener ->
                    val selected = mSpinner.selectedItem.toString()
                    val sellerData =
                        FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                            .child("Categories")
                    sellerData.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (i in snapshot.children) {
                                if (i.child("category").value.toString() == selected) {
                                    bool = true
                                    Toast.makeText(
                                        this@Category,
                                        "This category already exists",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
                    if (!bool) {
                        Toast.makeText(this@Category, selected, Toast.LENGTH_SHORT).show()
                        val headers = HashMap<String, Any>()
                        headers["categoryId"] = timestamp
                        headers["category"] = selected
                        databaseReference.child(uid).child("Categories").child(timestamp)
                            .setValue(headers).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this, "New Category Added", Toast.LENGTH_SHORT)
                                        .show()
                                    new.dismiss()
                                    this.recreate()
                                }
                            }
                    }
                }
                builder.setNegativeButton("Dismiss") { text, listener ->
                    new.dismiss()
                }
                builder.setView(view)
                builder.create().show()
            } else {
                rl_category.visibility = View.GONE
                rl_retryCategory.visibility = View.VISIBLE
            }
        }
        if (ConnectionManager().checkConnectivity(this)) {
            databaseReference.child(uid).child("Categories")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        (categories as ArrayList<ModelCategory>).clear()
                        for (i in snapshot.children) {
                            val obj = ModelCategory(
                                i.child("categoryId").value.toString(),
                                i.child("category").value.toString()
                            )
                            (categories as ArrayList<ModelCategory>).add(obj)
                        }

                        adapterCategory = AdapterCategory(this@Category, categories,uid)
                        recyclerCategories.adapter = adapterCategory

                    }
                })
        } else {
            rl_category.visibility = View.GONE
            rl_retryCategory.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}