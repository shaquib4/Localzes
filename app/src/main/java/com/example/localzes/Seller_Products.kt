package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterSellerProducts
import com.example.localzes.Modals.ModelAddProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_seller__products.*
import util.ConnectionManager

class Seller_Products : AppCompatActivity() {
    private lateinit var productDatabaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var mSellerProducts: List<ModelAddProduct>
    private lateinit var productAdapter: AdapterSellerProducts
    private lateinit var recyclerSellerProducts: RecyclerView
    private lateinit var relativeAddProduct: RelativeLayout
    private lateinit var search: EditText
    private lateinit var imgBackProducts: ImageView
    private lateinit var txtAddProduct: TextView
    private var category: String? = "400"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller__products)
        mSellerProducts = ArrayList<ModelAddProduct>()
        recyclerSellerProducts = findViewById(R.id.recycler_view_seller_products)
        // imgBackProducts=findViewById(R.id.imgBackProducts)
        //  txtAddProduct=findViewById(R.id.add)
        relativeAddProduct = findViewById(R.id.rl_Add_Products)
        category = intent.getStringExtra("category")
        recyclerSellerProducts.layoutManager = LinearLayoutManager(this)
        auth = FirebaseAuth.getInstance()
        search = findViewById(R.id.searchShopProduct)
        val user = auth.currentUser
        val uid = user!!.uid
        if (category.toString() == null) {
            additem.setOnClickListener {
                startActivity(Intent(this, AddProduct::class.java))
            }
        } else {
            additem.setOnClickListener {
                val intent = Intent(this, AddProduct::class.java)
                intent.putExtra("categoryAdd", category.toString())
                startActivity(intent)
            }
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(cs: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchSellerProducts(cs.toString().toLowerCase())
            }
        })

        sellerProductRetry.setOnClickListener {
            this.recreate()
        }

        bottom_navProducts.selectedItemId = R.id.nav_product_seller
        bottom_navProducts.setOnNavigationItemSelectedListener { item ->


            when (item.itemId) {


                R.id.nav_product_seller -> {

                    return@setOnNavigationItemSelectedListener true

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

                    startActivity(Intent(this, Category::class.java))
                    overridePendingTransition(0, 0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener false
        }
        productDatabaseRef =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Products")
        if (category != null) {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_sellerProducts.visibility = View.VISIBLE
                rl_Seller_Products_Internet.visibility = View.GONE
                productDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Seller_Products, error.message, Toast.LENGTH_SHORT)
                            .show()

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        (mSellerProducts as ArrayList<ModelAddProduct>).clear()
                        for (i in snapshot.children) {
                            val obj = ModelAddProduct(
                                i.child("shopId").value.toString(),
                                i.child("productId").value.toString(),
                                i.child("imageUrl").value.toString(),
                                i.child("productCategory").value.toString(),
                                i.child("title").value.toString(),
                                i.child("description").value.toString(),
                                i.child("sellingPrice").value.toString(),
                                i.child("offerPrice").value.toString(),
                                i.child("unit").value.toString(),
                                i.child("quantity").value.toString(),
                                i.child("stock").value.toString()
                            )
                            if (i.child("productCategory").value.toString() == category.toString()) {
                                (mSellerProducts as ArrayList<ModelAddProduct>).add(obj)
                            }
                        }
                        if (mSellerProducts.isEmpty()) {
                            recyclerSellerProducts.visibility = View.GONE
                        } else {
                            relativeAddProduct.visibility = View.GONE
                            recyclerSellerProducts.visibility = View.VISIBLE
                            productAdapter =
                                AdapterSellerProducts(
                                    this@Seller_Products,
                                    mSellerProducts
                                )
                            recyclerSellerProducts.adapter = productAdapter
                        }
                    }
                })
            } else {
                rl_sellerProducts.visibility = View.GONE
                rl_Seller_Products_Internet.visibility = View.VISIBLE
            }
        } else {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_sellerProducts.visibility = View.VISIBLE
                rl_Seller_Products_Internet.visibility = View.GONE
                productDatabaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Seller_Products, error.message, Toast.LENGTH_SHORT)
                            .show()

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        (mSellerProducts as ArrayList<ModelAddProduct>).clear()
                        for (i in snapshot.children) {
                            val obj = ModelAddProduct(
                                i.child("shopId").value.toString(),
                                i.child("productId").value.toString(),
                                i.child("imageUrl").value.toString(),
                                i.child("productCategory").value.toString(),
                                i.child("title").value.toString(),
                                i.child("description").value.toString(),
                                i.child("sellingPrice").value.toString(),
                                i.child("offerPrice").value.toString(),
                                i.child("unit").value.toString(),
                                i.child("quantity").value.toString(),
                                i.child("stock").value.toString()
                            )
                            (mSellerProducts as ArrayList<ModelAddProduct>).add(obj)
                        }
                        if (mSellerProducts.isEmpty()) {
                            recyclerSellerProducts.visibility = View.GONE
                        } else {
                            relativeAddProduct.visibility = View.GONE
                            recyclerSellerProducts.visibility = View.VISIBLE
                            productAdapter =
                                AdapterSellerProducts(
                                    this@Seller_Products,
                                    mSellerProducts
                                )
                            recyclerSellerProducts.adapter = productAdapter
                        }
                    }
                })
            } else {
                rl_sellerProducts.visibility = View.GONE
                rl_Seller_Products_Internet.visibility = View.VISIBLE
            }

        }
    }

    private fun searchSellerProducts(str: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user!!.uid
        val queryProduct =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Products")
                .orderByChild("title")
                .startAt(str)
                .endAt(str + "\uf8ff")
        if (ConnectionManager().checkConnectivity(this)) {
            rl_sellerProducts.visibility = View.VISIBLE
            rl_Seller_Products_Internet.visibility = View.GONE
            queryProduct.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    (mSellerProducts as ArrayList<ModelAddProduct>).clear()
                    for (i in snapshot.children) {
                        val obj = ModelAddProduct(
                            i.child("shopId").value.toString(),
                            i.child("productId").value.toString(),
                            i.child("imageUrl").value.toString(),
                            i.child("productCategory").value.toString(),
                            i.child("title").value.toString(),
                            i.child("description").value.toString(),
                            i.child("sellingPrice").value.toString(),
                            i.child("offerPrice").value.toString(),
                            i.child("unit").value.toString(),
                            i.child("quantity").value.toString(),
                            i.child("stock").value.toString()
                        )
                        (mSellerProducts as ArrayList<ModelAddProduct>).add(obj)

                    }
                    productAdapter =
                        AdapterSellerProducts(
                            this@Seller_Products,
                            mSellerProducts
                        )
                    recyclerSellerProducts.adapter = productAdapter
                }
            })
        } else {
            rl_sellerProducts.visibility = View.GONE
            rl_Seller_Products_Internet.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}