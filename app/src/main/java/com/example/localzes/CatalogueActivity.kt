package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.localzes.Modals.ModelAddProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CatalogueActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var catalogueList: List<ModelAddProduct>
    private lateinit var shopAuth: FirebaseAuth
    private var shopName=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogue)
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        databaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        databaseReference.child("Products").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
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
                    (catalogueList as ArrayList<ModelAddProduct>).add(obj)
                }
            }
        })
    }
}