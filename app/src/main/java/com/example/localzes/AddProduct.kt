package com.example.localzes

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_product.*

class AddProduct : AppCompatActivity() {
    private lateinit var imagePath: Uri
    private lateinit var products: ModelAddProduct
    private lateinit var mCartDatabaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        auth = FirebaseAuth.getInstance()
        image_view.setOnClickListener {
            startImageChooser()
        }

        btnAddProduct.setOnClickListener {
            uploadData()
            val intent=Intent(this,Seller_Products::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun startImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Product Image"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            imagePath = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
            image_view.setImageBitmap(bitmap)
        }
    }

    private fun uploadData() {
        if (imagePath != null) {
            val pd = ProgressDialog(this)
            pd.setTitle("Uploading.....")
            pd.show()
            val productRef =
                FirebaseStorage.getInstance().reference.child("uploads/" + System.currentTimeMillis() + ".jpg")
            productRef.putFile(imagePath)
                .addOnSuccessListener { p0 ->
                    pd.dismiss()
                    productRef.downloadUrl.addOnSuccessListener {
                        val imageUrl: Uri = it
                        val timestamp: String = "" + System.currentTimeMillis()
                        products = ModelAddProduct(
                            timestamp,
                            imageUrl.toString(),
                            sp_spinner_add.selectedItem.toString(),
                            etTittle.text.toString().trim(),
                            etDescription.text.toString().trim(),
                            etSellPrice.text.toString().trim(),
                            etOfferPrice.text.toString().trim(),
                            sp_unit.selectedItem.toString(),
                            etQuantity.text.toString().trim()
                        )

                        val user = auth.currentUser
                        val uid = user!!.uid

                        mCartDatabaseRef = FirebaseDatabase.getInstance().reference.child("seller")
                        mCartDatabaseRef.child(uid).child("Products").child(timestamp)
                            .setValue(products)
                        Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT)
                            .show()

                    }
                }
                .addOnFailureListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, p0.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { p0 ->
                    val progress = (100.0 * p0.bytesTransferred / p0.totalByteCount)
                    pd.setMessage("Uploading ${progress.toInt()}%")
                }
        }
    }
    
}