package com.localze.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.view.iterator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.localze.android.Modals.ModelProductDescription

class AddProductDetails : AppCompatActivity() {
    private lateinit var productDescription: EditText
    private lateinit var chooseColors: EditText
    private lateinit var inputSizes: EditText
    private lateinit var refundableType: Spinner
    private lateinit var detailDatabase: DatabaseReference
    private var productId: String? = null
    private lateinit var shopAuth: FirebaseAuth
    private lateinit var proceed: Button
    private lateinit var pickColor: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product_details2)
        productDescription = findViewById(R.id.edtDescription)
        chooseColors = findViewById(R.id.edtColor)
        inputSizes = findViewById(R.id.edtSize)
        refundableType = findViewById(R.id.sp_refund)
        proceed = findViewById(R.id.btnProceed)
        pickColor = findViewById(R.id.imgColor)
        productId = intent.getStringExtra("pId").toString()
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        detailDatabase =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Products")
                .child(productId.toString()).child("ProductDetails")
        detailDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    productDescription.setText(snapshot.child("description").value.toString())
                    chooseColors.setText(snapshot.child("colors").value.toString())
                    inputSizes.setText(snapshot.child("sizes").value.toString())
                    for (i in 0 until refundableType.count) {
                        if (refundableType.getItemAtPosition(i)
                                .toString() == snapshot.child("refundableType").value.toString()
                        ) {
                            refundableType.setSelection(i)
                        }
                    }
                    proceed.setOnClickListener {
                        val descriptionMap = HashMap<String, Any>()
                        descriptionMap["description"] = productDescription.text.toString()
                        descriptionMap["colors"] = chooseColors.text.toString()
                        descriptionMap["sizes"] = inputSizes.text.toString()
                        descriptionMap["refundableType"] = refundableType.selectedItem.toString()
                        detailDatabase.updateChildren(descriptionMap).addOnSuccessListener {
                            val intent = Intent(this@AddProductDetails, Seller_Products::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    proceed.setOnClickListener {
                        val descriptionMap = HashMap<String, Any>()
                        descriptionMap["description"] = productDescription.text.toString()
                        descriptionMap["colors"] = chooseColors.text.toString()
                        descriptionMap["sizes"] = inputSizes.text.toString()
                        descriptionMap["refundableType"] = refundableType.selectedItem.toString()
                        detailDatabase.updateChildren(descriptionMap).addOnSuccessListener {
                            val intent = Intent(this@AddProductDetails, Seller_Products::class.java)
                            startActivity(intent)
                        }
                    }
                }

            }

        })

    }

    override fun onBackPressed() {
        val intent = Intent(this, Seller_Products::class.java)
        startActivity(intent)
        finish()
    }
}