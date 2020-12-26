package com.example.localzes

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_seller_shop_detail.*
import kotlinx.android.synthetic.main.activity_update_product_details.*
import util.ConnectionManager
import java.io.ByteArrayOutputStream
import java.io.File

class UpdateProductDetailsActivity : AppCompatActivity() {
    private var imagePathUpdate: Uri? = null
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
    private lateinit var radioGroup: RadioGroup
    var thumb_Bitmap: Bitmap? = null
    var imgUrl: Uri? = null
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
        radioGroup = findViewById(R.id.radioStockCustomer)
        retryUpdateProductDetail.setOnClickListener {
            this.recreate()
        }

        productId = intent.getStringExtra("productId")
        databaseRef = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        if (ConnectionManager().checkConnectivity(this)){
            rl_retryUpdateProductDetail.visibility= View.GONE
            rl_productDetail.visibility=View.VISIBLE
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
            })}else{
            rl_retryUpdateProductDetail.visibility= View.VISIBLE
            rl_productDetail.visibility=View.GONE
        }
        imageUpdate.setOnClickListener {
            startImageChooser()
        }
        updateProduct.setOnClickListener {
            when {
                productName.text.toString().isEmpty() -> {
                    productName.error = "Please Enter Product Name"
                    return@setOnClickListener
                }
                offerPriceUpdate.text.toString().isEmpty() -> {
                    offerPriceUpdate.error = "Please Enter selling price of your Product"
                    return@setOnClickListener
                }
                sellPriceUpdate.text.toString().isEmpty() -> {
                    sellPriceUpdate.error = "Please Enter MRP of your Product"
                    return@setOnClickListener

                }
                quantityUpdate.text.toString().isEmpty() -> {
                    quantityUpdate.error = "Please Enter Quantity"
                    return@setOnClickListener
                }
                else -> {
                    if (ConnectionManager().checkConnectivity(this)){
                        rl_retryUpdateProductDetail.visibility= View.GONE
                        rl_productDetail.visibility=View.VISIBLE

                        updateData()
                    val intent = Intent(this, Seller_Products::class.java)
                    startActivity(intent)
                    finish()}else{

                        rl_retryUpdateProductDetail.visibility= View.VISIBLE
                        rl_productDetail.visibility=View.GONE
                    }
                }
            }
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
                    imagePathUpdate = result.uri
                    val path = File(imagePathUpdate?.path.toString())
                    try {
                        thumb_Bitmap = Compressor(this)
                            .setQuality(50)
                            .compressToBitmap(path)
                        val baos = ByteArrayOutputStream()
                        thumb_Bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
                        val final_image = baos.toByteArray()
                        val filePathName = "uploads/$productId.jpg"
                        val storageReference: StorageReference =
                            FirebaseStorage.getInstance().reference.child(filePathName)
                        val uploadTask = storageReference.putBytes(final_image)
                        uploadTask.addOnSuccessListener { taskSnapshot ->
                            val imageUri = taskSnapshot.storage.downloadUrl
                            imageUri.addOnSuccessListener {
                                imgUrl = it
                            }
                        }

                    } catch (e: Exception) {

                    }
                    result.uri?.let { uri ->
                        setImage(uri)
                    }
                }
            }
        }
    }

    private fun setImage(it: Uri) {
        Glide.with(this).load(it).into(imageUpdate)

    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(16, 10)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private fun updateData() {
        if (imagePathUpdate == null) {
            val headers = HashMap<String, Any>()
            headers["offerPrice"] = offerPriceUpdate.text.toString().trim()
            headers["productCategory"] = categoryUpdate.selectedItem.toString().trim()
            headers["quantity"] = quantityUpdate.text.toString().trim()
            headers["sellingPrice"] = sellPriceUpdate.text.toString().trim()
            headers["title"] = productName.text.toString().trim()
            headers["unit"] = unitUpdate.selectedItem.toString().trim()
            headers["description"] = descriptionUpdate.text.toString().trim()
            databaseRef.child("Products").child(productId.toString()).updateChildren(headers)
                .addOnSuccessListener {
                    Toast.makeText(this, "Item Updated Successfully", Toast.LENGTH_SHORT)
                        .show()
                }

        } else {
/*            val filePathName = "uploads/$productId.jpg"
            val user = updateAuth.currentUser
            val uid = user!!.uid
            val storageReference: StorageReference =
                FirebaseStorage.getInstance().reference.child(filePathName)
            storageReference.putFile(imagePathUpdate!!).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener {
                    val users = FirebaseAuth.getInstance().currentUser
                    val imageUrl: Uri = it
                    val request = UserProfileChangeRequest.Builder().setPhotoUri(it).build()
                    users?.updateProfile(request)?.addOnSuccessListener {
                        val headers = HashMap<String, Any>()
                        headers["imageUrl"] = imageUrl.toString()
                        headers["offerPrice"] = offerPriceUpdate.text.toString().trim()
                        headers["productCategory"] = categoryUpdate.selectedItem.toString().trim()
                        headers["quantity"] = quantityUpdate.text.toString().trim()
                        headers["sellingPrice"] = sellPriceUpdate.text.toString().trim()
                        headers["title"] = productName.text.toString().trim()
                        headers["unit"] = unitUpdate.selectedItem.toString().trim()
                        headers["description"] = descriptionUpdate.text.toString().trim()
                        databaseRef.child("Products").child(productId.toString())
                            .updateChildren(headers)
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
            }*/
            val users = FirebaseAuth.getInstance().currentUser
            val imageUrl = imgUrl
            val request = UserProfileChangeRequest.Builder().setPhotoUri(imgUrl).build()
            users?.updateProfile(request)?.addOnSuccessListener {
                val headers = HashMap<String, Any>()
                headers["imageUrl"] = imageUrl.toString()
                headers["offerPrice"] = offerPriceUpdate.text.toString().trim()
                headers["productCategory"] = categoryUpdate.selectedItem.toString().trim()
                headers["quantity"] = quantityUpdate.text.toString().trim()
                headers["sellingPrice"] = sellPriceUpdate.text.toString().trim()
                headers["title"] = productName.text.toString().trim()
                headers["unit"] = unitUpdate.selectedItem.toString().trim()
                headers["description"] = descriptionUpdate.text.toString().trim()
                databaseRef.child("Products").child(productId.toString())
                    .updateChildren(headers).addOnSuccessListener {
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

    override fun onBackPressed() {
        val intent = Intent(this, Seller_Products::class.java)
        startActivity(intent)
        finish()
    }
}
