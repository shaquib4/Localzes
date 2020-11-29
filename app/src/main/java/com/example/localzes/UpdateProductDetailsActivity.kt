package com.example.localzes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class UpdateProductDetailsActivity : AppCompatActivity() {
    private lateinit var imagePathUpdate: Uri
    private lateinit var databaseRef: DatabaseReference
    private lateinit var updateAuth: FirebaseAuth
    private var productId: String? = "100"
    private lateinit var imageUpdate: ImageView
    private lateinit var productName: EditText
    private lateinit var categoryUpdate: Spinner
    private lateinit var descriptionUpdate: EditText
    private lateinit var sellPriceUpdate: EditText
    private lateinit var offerPriceUpdate: EditText
    private lateinit var quantityUpdate: EditText
    private lateinit var unitUpdate: Spinner
    private lateinit var updateProduct: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product_details)
        updateAuth = FirebaseAuth.getInstance()
        val user = updateAuth.currentUser
        val uid = user!!.uid
        imageUpdate = findViewById(R.id.image_view_update)
        categoryUpdate = findViewById(R.id.sp_spinner_add_update)
        descriptionUpdate = findViewById(R.id.etDescriptionUpdate)
        productName = findViewById(R.id.etTittleUpdate)
        offerPriceUpdate = findViewById(R.id.etOfferPriceUpdate)
        sellPriceUpdate = findViewById(R.id.etSellPriceUpdate)
        unitUpdate = findViewById(R.id.sp_unit_update)
        quantityUpdate = findViewById(R.id.etQuantityUpdate)
        updateProduct = findViewById(R.id.btnUpdateProduct)
        productId = intent.getStringExtra("productId")
        databaseRef = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        databaseRef.child("Products").child(productId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.child("imageUrl").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val mrp = snapshot.child("sellingPrice").value.toString()
                    val sellingPrice = snapshot.child("offerPrice").value.toString()
                    val unit = snapshot.child("unit").value.toString()
                    val quantity = snapshot.child("quantity").value.toString()
                    Picasso.get().load(imageUrl).into(imageUpdate)
                    productName.setText(title)
                    if (description != null) {
                        descriptionUpdate.setText(description)
                    } else {
                        descriptionUpdate.setText("")
                    }
                    sellPriceUpdate.setText(mrp)
                    offerPriceUpdate.setText(sellingPrice)
                    quantityUpdate.setText(quantity)
                }
            })
        imageUpdate.setOnClickListener {
            startImageChooser()
        }
        updateProduct.setOnClickListener {
            updateData()
            val intent = Intent(this, Seller_Products::class.java)
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
            imagePathUpdate = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePathUpdate)
            imageUpdate.setImageBitmap(bitmap)
        }
    }

    private fun updateData() {
        if (imagePathUpdate == null) {
            val headers = HashMap<String, String>()
            headers["offerPrice"] = offerPriceUpdate.text.toString().trim()
            headers["productCategory"] = categoryUpdate.selectedItem.toString().trim()
            headers["quantity"] = quantityUpdate.text.toString().trim()
            headers["sellingPrice"] = sellPriceUpdate.text.toString().trim()
            headers["title"] = productName.text.toString().trim()
            headers["unit"] = unitUpdate.selectedItem.toString().trim()
            headers["description"] = descriptionUpdate.text.toString().trim()
            databaseRef.child("Products").child(productId.toString()).setValue(headers)
                .addOnSuccessListener {
                    Toast.makeText(this, "Item Updated Successfully", Toast.LENGTH_SHORT)
                        .show()
                }

        } else {
            val filePathName = "uploads/$productId.jpg"
            val user = updateAuth.currentUser
            val uid = user!!.uid
            val storageReference: StorageReference =
                FirebaseStorage.getInstance().reference.child(filePathName)
            storageReference.putFile(imagePathUpdate).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    val imageUrl: Uri = it
                    val request = UserProfileChangeRequest.Builder().setPhotoUri(it).build()
                    user.updateProfile(request).addOnSuccessListener {
                        val headers = HashMap<String, Any>()
                        headers["imageUrl"] = imageUrl.toString()
                        headers["offerPrice"] = offerPriceUpdate.text.toString().trim()
                        headers["productCategory"] = categoryUpdate.selectedItem.toString().trim()
                        headers["quantity"] = quantityUpdate.text.toString().trim()
                        headers["sellingPrice"] = sellPriceUpdate.text.toString().trim()
                        headers["title"] = productName.text.toString().trim()
                        headers["unit"] = unitUpdate.selectedItem.toString().trim()
                        headers["description"] = descriptionUpdate.text.toString().trim()
                        databaseRef.child("Products").child(productId.toString()).setValue(headers)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Item Updated Successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                    }
                }
            }
        }
    }
}
