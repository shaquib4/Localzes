package com.example.localzes

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
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

        auth= FirebaseAuth.getInstance()
        btnshare.setOnClickListener{ val myIntent=Intent(Intent.ACTION_SEND)
            myIntent.type="type/plain"
            myIntent.putExtra(Intent.EXTRA_SUBJECT,"Localze")
            myIntent.putExtra(Intent.EXTRA_TEXT,"Localze")
            startActivity(Intent.createChooser(myIntent,"Share App Via"))}



        image_view.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)

                } else {
                    pickImageFromGallery()
                }
            } else {
                pickImageFromGallery()
            }

            btnAddProduct.setOnClickListener {
                uploadData()
            }
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
                        val uid=user!!.uid

                        mCartDatabaseRef = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                        Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show()
                        clearData()
                    }
                }
                .addOnFailureListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(applicationContext, p0.message, Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener {p0->
                    val progress = (100.0 * p0.bytesTransferred / p0.totalByteCount)
                    pd.setMessage("Uploading ${progress.toInt()}%")
                }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)

    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imagePath = data?.data!!
            image_view.setImageURI(data.data)
        }
    }

    private fun clearData() {
        etTittle.setText("")
        etDescription.setText("")
        etSellPrice.setText("")
        etOfferPrice.setText("")
        etQuantity.setText("")
    }
}