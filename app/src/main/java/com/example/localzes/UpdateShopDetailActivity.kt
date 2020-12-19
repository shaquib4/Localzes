package com.example.localzes

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File

class UpdateShopDetailActivity : AppCompatActivity() {
    private var imagePathUpdated: Uri? = null
    private lateinit var databaseRef: DatabaseReference
    private lateinit var updateAuth: FirebaseAuth
    private lateinit var imageShopUpdate: ImageView
    private lateinit var shopCategoryUpdate: Spinner
    private lateinit var shopNameUpdate: EditText
    private lateinit var upiIdUpdate: EditText
    private lateinit var btnUpdateDetails: Button
    private lateinit var spinnerOpen: Spinner
    private lateinit var spinnerClose: Spinner
    private lateinit var spinnerClosingDay: Spinner
    var thumb_Bitmap: Bitmap? = null
    var imgUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_shop_detail)
        updateAuth = FirebaseAuth.getInstance()
        val user = updateAuth.currentUser
        val uid = user!!.uid
        imageShopUpdate = findViewById(R.id.choose_image_update)
        shopCategoryUpdate = findViewById(R.id.spn_category)
        shopNameUpdate = findViewById(R.id.edtNameUpdate)
        upiIdUpdate = findViewById(R.id.edtPayUpdate)
        btnUpdateDetails = findViewById(R.id.Update)
        spinnerClose = findViewById(R.id.spinner_close)
        spinnerOpen = findViewById(R.id.spinner_open)
        spinnerClosingDay = findViewById(R.id.spinner_closing_day)
        databaseRef = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val imageUrl = snapshot.child("imageUrl").value.toString()
                val shopName = snapshot.child("shop_name").value.toString()
                val shopCategory = snapshot.child("category1").value.toString()
                val upiId = snapshot.child("upi").value.toString()
                Picasso.get().load(imageUrl).into(imageShopUpdate)
                shopNameUpdate.setText(shopName)
                upiIdUpdate.setText(upiId)
            }
        })

        imageShopUpdate.setOnClickListener {
            startImageChooser()
        }
        btnUpdateDetails.setOnClickListener {
            updateData()
            val intent = Intent(this, Home_seller::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateData() {
        if (imagePathUpdated == null) {
            val headers = HashMap<String, Any>()
            headers["category1"] = shopCategoryUpdate.selectedItem.toString().trim()
            headers["shop_name"] = shopNameUpdate.text.toString().trim()
            headers["upi"] = upiIdUpdate.text.toString().trim()
            headers["openingTime"] = spinnerOpen.selectedItem.toString().trim()
            headers["closingTime"] = spinnerClose.selectedItem.toString().trim()
            headers["closingDay"] = spinnerClosingDay.selectedItem.toString().trim()
            databaseRef.updateChildren(headers).addOnSuccessListener {
                Toast.makeText(this, "Shop Updated Successfully", Toast.LENGTH_SHORT).show()
            }

        } else {
            /*val user = FirebaseAuth.getInstance().currentUser

            val uid = user!!.uid
            val shopRef =
                FirebaseStorage.getInstance().reference.child(
                    "uploads/" + uid
                            + ".jpg"
                )
            shopRef.putFile(imagePathUpdated!!).addOnSuccessListener {
                shopRef.downloadUrl.addOnSuccessListener {
                    val imageUrl: Uri = it
                    val request = UserProfileChangeRequest.Builder().setPhotoUri(it).build()
                    user.updateProfile(request).addOnSuccessListener {
                        val headers = HashMap<String, Any>()
                        headers["imageUrl"] = imageUrl.toString()
                        headers["category1"] = shopCategoryUpdate.selectedItem.toString().trim()
                        headers["shop_name"] = shopNameUpdate.text.toString().trim()
                        headers["upi"] = upiIdUpdate.text.toString().trim()
                        headers["openingTime"] = spinnerOpen.selectedItem.toString().trim()
                        headers["closingTime"] = spinnerClose.selectedItem.toString().trim()
                        headers["closingDay"] = spinnerClosingDay.selectedItem.toString().trim()
                        databaseRef.updateChildren(headers).addOnSuccessListener {
                            Toast.makeText(this, "Shop Updated Successfully", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                }
            }*/
            val user = FirebaseAuth.getInstance().currentUser
            val imageUrl = imgUri
            val request = UserProfileChangeRequest.Builder().setPhotoUri(imageUrl).build()
            user?.updateProfile(request)?.addOnSuccessListener {
                val headers = HashMap<String, Any>()
                headers["imageUrl"] = imageUrl.toString()
                headers["category1"] = shopCategoryUpdate.selectedItem.toString().trim()
                headers["shop_name"] = shopNameUpdate.text.toString().trim()
                headers["upi"] = upiIdUpdate.text.toString().trim()
                headers["openingTime"] = spinnerOpen.selectedItem.toString().trim()
                headers["closingTime"] = spinnerClose.selectedItem.toString().trim()
                headers["closingDay"] = spinnerClosingDay.selectedItem.toString().trim()
                databaseRef.updateChildren(headers).addOnSuccessListener {
                    Toast.makeText(this, "Shop Updated Successfully", Toast.LENGTH_SHORT)
                        .show()
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
                        launchCropImage(uri)
                    }
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    imagePathUpdated = result.uri
                    val path = File(imagePathUpdated?.path.toString())
                    try {
                        thumb_Bitmap = Compressor(this)
                            .setQuality(75)
                            .compressToBitmap(path)
                        val baos = ByteArrayOutputStream()
                        thumb_Bitmap?.compress(Bitmap.CompressFormat.JPEG, 75, baos)
                        val compressedImage = baos.toByteArray()
                        val user = FirebaseAuth.getInstance().currentUser

                        val uid = user!!.uid
                        val shopRef =
                            FirebaseStorage.getInstance().reference.child(
                                "uploads/" + uid
                                        + ".jpg"
                            )
                        val uploadTask: UploadTask = shopRef.putBytes(compressedImage)
                        uploadTask.addOnSuccessListener { taskSnapshot ->
                            val imageUri = taskSnapshot.storage.downloadUrl
                            imageUri.addOnSuccessListener {
                                imgUri = it
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
        Glide.with(this).load(it).into(imageShopUpdate)

    }

    private fun launchCropImage(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(16, 5)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}