package com.example.localzes

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_seller_shop_detail.*

class SellerShop_detail : AppCompatActivity() {
    private lateinit var btnChooseImage: ImageView
    private lateinit var btnUpload: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var filepath: Uri
    private lateinit var mDatabaseRef: DatabaseReference
    private lateinit var etShopName: EditText
    private lateinit var btnSave: Button
    private lateinit var etCategory1: Spinner
    private lateinit var etCategory2: EditText
    private lateinit var etCategory3: EditText
    private lateinit var upi: EditText
    private lateinit var upload: Upload
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_shop_detail)
        btnChooseImage = findViewById(R.id.choose_image)
        btnUpload = findViewById(R.id.upload)

        etShopName = findViewById(R.id.edtName)
        etCategory1 = findViewById(R.id.spn_category)
        upi=findViewById(R.id.edtPay)
        auth= FirebaseAuth.getInstance()

        btnChooseImage.setOnClickListener {

            startFileChooser()

        }
        btnUpload.setOnClickListener {

            val user = auth.currentUser
            var uid=user!!.uid

            mDatabaseRef = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
            uploadFile()
        }

    }
    private fun uploadFile() {
        if (filepath != null) {
            val pd = ProgressDialog(this)
            pd.setTitle("Uploading")
            pd.show()

            val imageRef =
                FirebaseStorage.getInstance().reference.child("uploads/" + System.currentTimeMillis() + ".jpg")
            imageRef.putFile(filepath)
                .addOnSuccessListener { p0 ->
                    pd.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "FileUploadedSucessfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    imageRef.downloadUrl.addOnSuccessListener {
                        val downloadUrl: Uri = it
                        val name=intent.getStringExtra("name")
                        val email=intent.getStringExtra("email")
                        val address=intent.getStringExtra("address")
                        upload = Upload(
                            name!!.toString().trim(),
                            email!!.toString().trim(),
                            address!!.toString().trim(),
                            etShopName.text.toString().trim(),
                            downloadUrl.toString(),
                            etCategory1.selectedItem.toString(),
                            upi.text.toString().trim()

                        )

                        mDatabaseRef.setValue(upload).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                startActivity(Intent(this@SellerShop_detail,AddProduct::class.java))
                                finish()
                                Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                            }
                        }

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
