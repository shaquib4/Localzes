package com.example.localzes

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.example.localzes.Modals.ModelClass
import com.example.localzes.Modals.Upload
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.HashMap

class SellerShop_detail : AppCompatActivity() {
    private lateinit var btnChooseImage: ImageView
    private lateinit var btnUpload: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var filepath: Uri
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_shop_detail)
        btnChooseImage = findViewById(R.id.choose_image)
        btnUpload = findViewById(R.id.upload)
        openingTime=findViewById(R.id.openTime)
        closingTime=findViewById(R.id.closeTime)
        closingDay=findViewById(R.id.closeDay)
        etShopName = findViewById(R.id.edtName)
        etCategory1 = findViewById(R.id.spn_category)
        upi = findViewById(R.id.edtPay)
        auth = FirebaseAuth.getInstance()


        btnChooseImage.setOnClickListener {

            startFileChooser()

        }
        btnUpload.setOnClickListener {

            val user = auth.currentUser
            val uid = user!!.uid



            uploadFile()
        }

    }

    private fun uploadFile() {
        if (filepath != null) {
            val pd = ProgressDialog(this)
            pd.setTitle("Uploading")
            pd.show()
            val user = auth.currentUser
            val uid = user!!.uid
            val imageRef =
                FirebaseStorage.getInstance().reference.child(
                    "uploads/" + uid
                            + ".jpg"
                )
            imageRef.putFile(filepath)
                .addOnSuccessListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "FileUploadedSuccessfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    imageRef.downloadUrl.addOnSuccessListener {
                        val user = auth.currentUser
                        val uid = user!!.uid
                        userDatabases =
                            FirebaseDatabase.getInstance().reference.child("customers").child(uid)
                        userDatabases!!.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {

                                    val users: ModelClass? = snapshot.getValue(
                                        ModelClass::class.java
                                    )
                                    val phone: String? = users!!.getPhone()
                                    val downloadUrl: Uri = it
                                    val name = intent.getStringExtra("name")
                                    val email = intent.getStringExtra("email")
                                    val address = intent.getStringExtra("address")
                                    val city = intent.getStringExtra("city")
                                    val state = intent.getStringExtra("state")
                                    val country = intent.getStringExtra("country")
                                    val pinCode = intent.getStringExtra("pinCode")
                                    val locality = intent.getStringExtra("locality")
                                    val locality2=intent.getStringExtra("locality2")
                                    val nearestLandmark=intent.getStringExtra("nearestLandmark")
                                    val houseNo=intent.getStringExtra("HouseNo")

                                    upload = Upload(
                                        uid,
                                        phone.toString().trim(),
                                        name!!.toString().trim(),
                                        email!!.toString().trim(),
                                        address!!.toString().trim(),
                                        etShopName.text.toString().trim(),
                                        downloadUrl.toString(),
                                        etCategory1.selectedItem.toString(),
                                        upi.text.toString().trim(),
                                        locality!!.toString().trim().toLowerCase(),
                                        city!!.toString().trim().toLowerCase(),
                                        pinCode!!.toString().trim(),
                                        state!!.toString().trim(),
                                        country!!.toString().trim(),
                                        openingTime.selectedItem.toString(),
                                        closingTime.selectedItem.toString(),
                                        closingDay.selectedItem.toString()
                                    )
                                    mDatabaseRef =
                                        FirebaseDatabase.getInstance().reference.child("seller")
                                            .child(uid)
                                    mDatabaseRef.setValue(upload).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            mDatabaseRef.child("StoreStatus").setValue("OPEN")
                                            /*val headers = HashMap<String, String>()
                                            headers["viewCount"] = 0.toString()
                                            mDatabaseRef =
                                                FirebaseDatabase.getInstance().reference.child("seller")
                                                    .child(uid).child("shopViews")
                                            mDatabaseRef.setValue(headers)*/

                                            mDatabaseRef =
                                                FirebaseDatabase.getInstance().reference.child("seller")
                                                    .child(uid).child("current_address")
                                            val userMaps = HashMap<String, Any>()
                                            userMaps["address"] = address.toString()
                                            userMaps["city"] = city!!.toString()
                                            userMaps["state"] = state.toString()
                                            userMaps["country"] = country.toString()
                                            userMaps["pinCode"] = pinCode.toString()
                                            userMaps["locality2"]=locality2.toString()
                                            userMaps["nearestLandmark"]=nearestLandmark.toString()
                                            userMaps["House/Block"]=houseNo.toString()
                                            mDatabaseRef.setValue(userMaps)
                                                .addOnCompleteListener { task ->
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

                            override fun onCancelled(error: DatabaseError) {

                            }


                        })


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

    private fun startFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Choose Image"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            filepath = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            btnChooseImage.setImageBitmap(bitmap)
        }
    }
}
