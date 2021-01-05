package com.example.localzes

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.localzes.Modals.ModelAddProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_product.*
import util.ConnectionManager
import java.io.ByteArrayOutputStream
import java.io.File

class AddProduct : AppCompatActivity() {
    private var imagePath: Uri? = null
    private lateinit var progressDialog: ProgressDialog
    private lateinit var products: ModelAddProduct
    private lateinit var mCartDatabaseRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var radioGroup: RadioGroup
    private lateinit var imgBackAdd: ImageView

    /*private lateinit var thumb_reference: StorageReference*/
    private lateinit var timestamp: String
    private var categorySelected: String? = "200"
    var imgUrl: String = ""
    private var spinnerItem: String = ""

    var thumb_Bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        auth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        timestamp = System.currentTimeMillis().toString()
        image_view.setOnClickListener {
            startImageChooser()
        }
        categorySelected = intent.getStringExtra("categoryAdd")
        /*thumb_reference = FirebaseStorage.getInstance().reference.child("thumb_images")*/
        imgBackAdd = findViewById(R.id.imgBackAdd)
        radioGroup = findViewById(R.id.radioStock)
        spinnerItem = if (categorySelected.toString() == null) {
            sp_spinner_add.selectedItem.toString()
        } else {
            sp_spinner_add.setSelection(getIndex(sp_spinner_add, categorySelected.toString()))
                .toString()
        }
        imgBackAdd.setOnClickListener {
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
        retryAddProduct.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_addProduct.visibility = View.VISIBLE
                rl_retryAddProduct.visibility = View.GONE
                this.recreate()
            } else {
                rl_addProduct.visibility = View.GONE
                rl_retryAddProduct.visibility = View.VISIBLE
            }
        }

        btnAddProduct.setOnClickListener {
            when {
                etTittle.text.trim().toString().isEmpty() -> {
                    etTittle.error = "Please enter product name"
                    return@setOnClickListener
                }
                etSellPrice.text.trim().toString().isEmpty() -> {
                    etSellPrice.error = "Please enter MRP of product"
                    return@setOnClickListener
                }
                etOfferPrice.text.trim().toString().isEmpty() -> {
                    etOfferPrice.error = "Please enter selling price of product"
                    return@setOnClickListener
                }
                etQuantity.text.trim().toString().isEmpty() -> {
                    etQuantity.error = "Please enter quantity"
                    return@setOnClickListener
                }
                imagePath == null -> {

                    Toast.makeText(this, "Please provide image of Your product", Toast.LENGTH_LONG)
                        .show()
                }
                (etOfferPrice.text.trim().toString().toDouble() > etSellPrice.text.trim().toString()
                    .toDouble()) -> {
                    Toast.makeText(
                        this,
                        "Selling price should be less than MRP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    if (ConnectionManager().checkConnectivity(this)) {
                        rl_addProduct.visibility = View.VISIBLE
                        rl_retryAddProduct.visibility = View.GONE
                        progressDialog.setMessage("Adding Your Product....")
                        progressDialog.show()
                        val id = radioGroup.checkedRadioButtonId
                        val radioButton = findViewById<RadioButton>(id)
                        val stock = radioButton.text
                        uploadData(stock, progressDialog)
                    } else {
                        rl_addProduct.visibility = View.GONE
                        rl_retryAddProduct.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun getIndex(mySpinner: Spinner, myValue: String): Int {
        for (i in 0 until mySpinner.count) {
            if (mySpinner.getItemAtPosition(i).toString().equals(myValue, ignoreCase = true)) {
                return i
            }
        }
        return 0
    }

    private fun startImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Product Image"), 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    imagePath = result.uri
                    val path = File(imagePath!!.path.toString())
                    try {
                        thumb_Bitmap = Compressor(this)
                            .setQuality(50)
                            .compressToBitmap(path)
                        val baos = ByteArrayOutputStream()
                        thumb_Bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                        val final_image = baos.toByteArray()
                        /*image_view.setImageURI(imagePath)*/
                        /* val thumb_filepath = thumb_reference.child("$timestamp.jpg")*/
                        val productRef =
                            FirebaseStorage.getInstance().reference.child("uploads/$timestamp.jpg")
                        val uploadTask: UploadTask = productRef.putBytes(final_image)
                        uploadTask.addOnSuccessListener { taskSnapshot ->
                            val imageUri = taskSnapshot.storage.downloadUrl
                            imageUri.addOnSuccessListener {
                                imgUrl = it.toString()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    result.uri?.let {
                        setImage(it)
                    }
                }
            }
        }
    }

    private fun setImage(it: Uri) {
        Glide.with(this).load(it).into(image_view)
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(16, 10)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)


    }

    private fun uploadData(stock: CharSequence, progressDialog: ProgressDialog) {
        try {
            if (imagePath != null) {
/*
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

                }*/
                val user = auth.currentUser
                val uid = user!!.uid
                products = ModelAddProduct(
                    uid,
                    timestamp,
                    imgUrl,
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
                    .setValue(products).addOnCompleteListener {
                        if (it.isSuccessful) {
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
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Please Try Again", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }

}