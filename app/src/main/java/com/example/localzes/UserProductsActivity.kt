package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class UserProductsActivity : AppCompatActivity() {
    private var shopId: String? = "100"
    private lateinit var mUserProductDatabase: DatabaseReference
    private lateinit var mUserProducts:List<ModelAddProduct>
    private lateinit var recyclerUserProduct:RecyclerView
    private lateinit var userProductAdapter:AdapterSellerProducts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_products)
        shopId = intent.getStringExtra("shopId")
        mUserProducts=ArrayList<ModelAddProduct>()
        recyclerUserProduct=findViewById(R.id.recycler_shop_user_products)
        recyclerUserProduct.layoutManager=LinearLayoutManager(this)
        mUserProductDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(shopId.toString())
        mUserProductDatabase.child("Products").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (mUserProducts as ArrayList<ModelAddProduct>).clear()
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
                    (mUserProducts as ArrayList<ModelAddProduct>).add(obj)
                }
                userProductAdapter=AdapterSellerProducts(this@UserProductsActivity,mUserProducts)
                recyclerUserProduct.adapter=userProductAdapter
            }

        })
    }
}