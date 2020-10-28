package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Seller_Products : AppCompatActivity() {
    private lateinit var productDatabaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var mSellerProducts: List<ModelAddProduct>
    private lateinit var productAdapter: AdapterSellerProducts
    private lateinit var recyclerSellerProducts: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller__products)
        mSellerProducts = ArrayList<ModelAddProduct>()
        recyclerSellerProducts = findViewById(R.id.recycler_view_seller_products)
        recyclerSellerProducts.layoutManager=LinearLayoutManager(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        productDatabaseRef =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Products")
        productDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Seller_Products,error.message,Toast.LENGTH_SHORT).show()

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val obj = ModelAddProduct(
                        i.child("productId").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("productCategory").value.toString(),
                        i.child("title").value.toString(),
                        i.child("description").value.toString(),
                        i.child("sellingPrice").value.toString(),
                        i.child("offerPrice").value.toString(),
                        i.child("unit").value.toString(),
                        i.child("quantity").value.toString()
                    )
                    (mSellerProducts as ArrayList<ModelAddProduct>).add(obj)
                }
                productAdapter = AdapterSellerProducts(this@Seller_Products, mSellerProducts)
                recyclerSellerProducts.adapter = productAdapter
            }
        })
    }
}