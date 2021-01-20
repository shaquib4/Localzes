package com.localzes.android

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.localzes.android.Modals.ModelClass
import com.localzes.android.Modals.Upload
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_seller_shop_detail.*
import util.ConnectionManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.HashMap

class SellerShop_detail : AppCompatActivity() {
    private lateinit var btnChooseImage: ImageView
    private lateinit var btnUpload: Button
    private lateinit var auth: FirebaseAuth
    private var filepath: Uri? = null
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var userDatabases: DatabaseReference
    private lateinit var etShopName: EditText
    private lateinit var btnSave: Button
    private lateinit var etCategory1: Spinner
    private lateinit var openingTime: Spinner
    private lateinit var closingTime: Spinner
    private lateinit var closingDay: Spinner
    private lateinit var etCategory2: EditText
    private lateinit var etCategory3: EditText
    private lateinit var upi: EditText
    private lateinit var upload: Upload
    var thumb_Bitmap: Bitmap? = null
    var imgUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_shop_detail)
        btnChooseImage = findViewById(R.id.choose_image)
        btnUpload = findViewById(R.id.upload)
        openingTime = findViewById(R.id.openTime)
        closingTime = findViewById(R.id.closeTime)
        closingDay = findViewById(R.id.closeDay)
        etShopName = findViewById(R.id.edtName)
        etCategory1 = findViewById(R.id.spn_category)
        upi = findViewById(R.id.edtPay)
        auth = FirebaseAuth.getInstance()


        retryShopDetail.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_ShopDetail.visibility = View.VISIBLE
                rl_retryShopDetail.visibility = View.GONE
                this.recreate()
            } else {
                rl_ShopDetail.visibility = View.VISIBLE
                rl_retryShopDetail.visibility = View.GONE
            }
        }
        btnChooseImage.setOnClickListener {
            startFileChooser()
        }
        btnUpload.setOnClickListener {
            when {
                etShopName.text.trim().toString().isEmpty() -> {
                    etShopName.error = "Please Enter Your Shop Name"
                    return@setOnClickListener
                }
                filepath == null -> {
                    Toast.makeText(this, "Please Provide Your Shop Image", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    if (ConnectionManager().checkConnectivity(this)) {
                        rl_ShopDetail.visibility = View.VISIBLE
                        rl_retryShopDetail.visibility = View.GONE
                        if (imgUrl==""){
                            Toast.makeText(this,"Slow Internet,Press Upload again",Toast.LENGTH_SHORT).show()
                        }else{
                            uploadFile()
                        }

                    } else {
                        rl_ShopDetail.visibility = View.VISIBLE
                        rl_retryShopDetail.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun uploadFile() {
        try {
            if (filepath != null) {
                val pd = ProgressDialog(this)
                pd.setTitle("Uploading")
                pd.show()
                val user = auth.currentUser
                val uid = user!!.uid
                userDatabases =
                    FirebaseDatabase.getInstance().reference.child("customers").child(uid)
                userDatabases.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val users: ModelClass? = snapshot.getValue(
                                ModelClass::class.java
                            )
                            val phone: String? = users!!.getPhone()
                            val downloadUrl = imgUrl
                            val name = intent.getStringExtra("name")
                            val email = intent.getStringExtra("email")
                            val address = intent.getStringExtra("address")
                            val city = intent.getStringExtra("city")
                            val state = intent.getStringExtra("state")
                            val country = intent.getStringExtra("country")
                            val pinCode = intent.getStringExtra("pinCode")
                            val locality = intent.getStringExtra("locality")
                            val locality2 = intent.getStringExtra("locality2")
                            val nearestLandmark = intent.getStringExtra("nearestLandmark")
                            val houseNo = intent.getStringExtra("HouseNo")
                            upload = Upload(
                                uid,
                                phone.toString().trim(),
                                name!!.toString().trim(),
                                email!!.toString().trim(),
                                address!!.toString().trim(),
                                etShopName.text.toString().trim().toLowerCase(),
                                downloadUrl,
                                etCategory1.selectedItem.toString(),
                                upi.text.toString().trim(),
                                locality!!.toString().trim().toLowerCase(),
                                city!!.toString().trim().toLowerCase(),
                                pinCode!!.toString().trim(),
                                state!!.toString().trim(),
                                country!!.toString().trim(),
                                openingTime.selectedItem.toString(),
                                closingTime.selectedItem.toString(),
                                closingDay.selectedItem.toString(),
                                locality2!!.toString().trim().toLowerCase()
                            )
                            mDatabaseRef =
                                FirebaseDatabase.getInstance().reference.child("seller")
                                    .child(uid)
                            mDatabaseRef.setValue(upload).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val timestamp = System.currentTimeMillis().toString()
                                    val categoryMap = HashMap<String, Any>()
                                    categoryMap["categoryId"] = timestamp
                                    categoryMap["category"] = etCategory1.selectedItem.toString()
                                    mDatabaseRef.child("Categories").child(timestamp)
                                        .setValue(categoryMap)
                                    mDatabaseRef.child("StoreStatus").setValue("OPEN")
                                    mDatabaseRef =
                                        FirebaseDatabase.getInstance().reference.child("seller")
                                            .child(uid).child("current_address")
                                    val userMaps = HashMap<String, Any>()
                                    userMaps["address"] = address.toString()
                                    userMaps["city"] = city.toString()
                                    userMaps["state"] = state.toString()
                                    userMaps["country"] = country.toString()
                                    userMaps["pinCode"] = pinCode.toString()
                                    userMaps["locality2"] = locality2.toString()
                                    userMaps["nearestLandmark"] = nearestLandmark.toString()
                                    userMaps["houseBlock"] = houseNo.toString()
                                    userMaps["mobileNo"] = phone.toString()
                                    mDatabaseRef.setValue(userMaps).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            startActivity(
                                                Intent(
                                                    this@SellerShop_detail,
                                                    AddProduct::class.java
                                                )
                                            )
                                            finish()
                                            Toast.makeText(
                                                applicationContext,
                                                "Uploaded Successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                baseContext, "Failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,"Please Try Again",Toast.LENGTH_SHORT).show()
        }

    }

    private fun startFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Choose Image"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            111 -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchCropImage(uri)
                    }
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    val result = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        filepath = result.uri
                        val path = File(filepath?.path.toString())
                        try {
                            thumb_Bitmap = Compressor(this)
                                .setQuality(55)
                                .compressToBitmap(path)
                            val baos = ByteArrayOutputStream()
                            thumb_Bitmap?.compress(Bitmap.CompressFormat.JPEG, 55, baos)
                            val compressedImage = baos.toByteArray()
                            val user = auth.currentUser
                            val uid = user!!.uid
                            val imageRef =
                                FirebaseStorage.getInstance().reference.child(
                                    "uploads/" + uid
                                            + ".jpg"
                                )
                            val uploadTask: UploadTask = imageRef.putBytes(compressedImage)
                            uploadTask.addOnSuccessListener { taskSnapshot ->
                                val imageUri = taskSnapshot.storage.downloadUrl
                                imageUri.addOnSuccessListener {
                                    imgUrl = it.toString()
                                }

                            }

                        } catch (e: Exception) {

                        }
                        result.uri?.let {
                            setImage(it)
                        }
                    }
                }
            }
        }
    }

    private fun setImage(uri: Uri) {
        Glide.with(this).load(uri).into(btnChooseImage)

    }

    private fun launchCropImage(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(16, 11)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)

    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
