package com.example.localzes

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterCategory
import com.example.localzes.Modals.ModelCategory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_orders_seller.*

class Category : AppCompatActivity() {
    private lateinit var btnAddNewCategory: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var shopAuth: FirebaseAuth
    private lateinit var recyclerCategories: RecyclerView
    private var timestamp: String = ""
    private lateinit var adapterCategory: AdapterCategory
    private lateinit var categories: List<ModelCategory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        timestamp = System.currentTimeMillis().toString()
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory)
        recyclerCategories = findViewById(R.id.recycler_Category)
        categories = ArrayList<ModelCategory>()
        recyclerCategories.layoutManager = GridLayoutManager(this, 2)
        databaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Categories")
        btnAddNewCategory.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val new = builder.create()
            val view: View = LayoutInflater.from(this).inflate(R.layout.dialog_spinner, null, false)
            builder.setTitle("Choose a new Category")
            val mSpinner = view.findViewById<Spinner>(R.id.choose_new_category)
            val adapter = ArrayAdapter<String>(
                this@Category,
                android.R.layout.simple_spinner_item,
                resources.getStringArray(R.array.Spinner_Category)
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mSpinner.adapter = adapter
            builder.setPositiveButton("Ok") { text, listener ->
                val selected = mSpinner.selectedItem.toString()
                val headers = HashMap<String, Any>()
                headers["categoryId"] = timestamp
                headers["category"] = selected
                databaseReference.child(timestamp).setValue(headers)
                Toast.makeText(this, "New Category Added", Toast.LENGTH_SHORT).show()
                new.dismiss()
            }
            builder.setNegativeButton("Dismiss") { text, listener ->
                new.dismiss()
            }
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
        databaseReference.addValueEventListener(object : ValueEventListener {
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
                if (categories.isEmpty()) {

                } else {
                    adapterCategory = AdapterCategory(this@Category, categories)
                    recyclerCategories.adapter = adapterCategory
                }
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}