package com.example.localzes

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.localzes.Modals.ModelAddProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_add_product.*

class AddProduct : AppCompatActivity() {
    private lateinit var imagePath: Uri
    private lateinit var progressDialog: ProgressDialog
    private lateinit var products: ModelAddProduct
    private lateinit var mCartDatabaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var radioGroup: RadioGroup
    private lateinit var imgBackAdd: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        image_view.setOnClickListener {
            startImageChooser()
        }
        radioGroup = findViewById(R.id.radioStock)
        imgBackAdd.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }

        btnAddProduct.setOnClickListener {
            progressDialog.setMessage("Adding Your Product....")
            progressDialog.show()
            val id = radioGroup.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(id)
            val stock = radioButton.text
            uploadData(stock, progressDialog)

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

    private fun uploadData(stock: CharSequence, progressDialog: ProgressDialog) {
        if (imagePath != null) {
            val timestamp: String = System.currentTimeMillis().toString()
            val productRef =
                FirebaseStorage.getInstance().reference.child("uploads/$timestamp.jpg")
            productRef.putFile(imagePath)
                .addOnSuccessListener {
                    productRef.downloadUrl.addOnSuccessListener {
                        val imageUrl: Uri = it
                        val user = auth.currentUser
                        val uid = user!!.uid
                        products = ModelAddProduct(
                            uid,
                            timestamp,
                            imageUrl.toString(),
                            sp_spinner_add.selectedItem.toString(),
                            etTittle.text.toString().trim().toLowerCase(),
                            etDescription.text.toString().trim(),
                            etSellPrice.text.toString().trim(),
                            etOfferPrice.text.toString().trim(),
                            sp_unit.selectedItem.toString(),
                            etQuantity.text.toString().trim(),
                            stock.toString()
                        )



                        mCartDatabaseRef = FirebaseDatabase.getInstance().reference.child("seller")
                        mCartDatabaseRef.child(uid).child("Products").child(timestamp)
                            .setValue(products).addOnSuccessListener {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    this,
                                    "Product Added Successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                val intent = Intent(this, Home_seller::class.java)
                                startActivity(intent)
                                finish()
                            }

                    }
                }
                .addOnFailureListener { p0 ->

                    Toast.makeText(applicationContext, p0.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { p0 ->
                    val progress = (100.0 * p0.bytesTransferred / p0.totalByteCount)

                }
        }
    }

}