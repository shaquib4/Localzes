package com.example.localzes

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import java.io.ByteArrayOutputStream
import java.io.File

class RegisterDetailDelivery : AppCompatActivity() {
    private val IMAGE_CAPTURE_CODE: Int = 1001
    var thumb_Bitmap: Bitmap? = null
    private lateinit var profileImage: ImageView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var profileName: EditText
    private lateinit var profileAge: EditText
    private lateinit var profilePermanentAddress: EditText
    private lateinit var temporaryAddress: EditText
    private lateinit var alternateMobileNo: EditText
    private lateinit var saveAndProceed: Button
    private lateinit var auth: FirebaseAuth
    val PERMISSION_CODE: Int = 1000
    var imageUri: Uri? = null
    var imgUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_detail_delivery)
        profileImage = findViewById(R.id.imageBoy)
        profileName = findViewById(R.id.your_profile_image)
        profileAge = findViewById(R.id.your_age)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
        temporaryAddress = findViewById(R.id.temporaryAddress)
        profilePermanentAddress = findViewById(R.id.permanentAddress)
        alternateMobileNo = findViewById(R.id.alternateMobileNo)
        saveAndProceed = findViewById(R.id.saveDeliveryDetails)
        profileImage.setOnClickListener {
            takeImageWithCamera()
        }
        saveAndProceed.setOnClickListener {
            when {
                profileName.text.trim().toString().isEmpty() -> {
                    profileName.error = "Please Enter Your Name"
                    return@setOnClickListener
                }
                profileAge.text.trim().toString().isEmpty() -> {
                    profileAge.error = "Please provide your age details"
                    return@setOnClickListener
                }
                profilePermanentAddress.text.trim().toString().isEmpty() -> {
                    profilePermanentAddress.error = "Please provide your age details"
                    return@setOnClickListener
                }
                alternateMobileNo.text.trim().toString().isEmpty() -> {
                    alternateMobileNo.error = "Please provide a alternate number"
                    return@setOnClickListener
                }
                (profileAge.text.trim().toString().toDouble() < 18.toDouble()) -> {
                    Toast.makeText(this, "Age should be greater than 18 yrs", Toast.LENGTH_SHORT)
                        .show()
                }
                imageUri == null -> {
                    Toast.makeText(this, "Please provide your image", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    progressDialog.setMessage("Creating Your Profile")
                    progressDialog.show()
                    uploadData(progressDialog)
                }
            }
        }
    }

    private fun uploadData(progressDialog: ProgressDialog) {
        try {
            if(imageUri!=null){
                val user=auth.currentUser
                val uid=user!!.uid

            }
        } catch (e: Exception) {

        }

    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(10, 10)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }

    private fun takeImageWithCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }

    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()

                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1001 -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                val user = auth.currentUser
                val uid = user!!.uid
                if (resultCode == Activity.RESULT_OK) {
                    imageUri = result.uri
                    val path = File(imageUri!!.path.toString())
                    try {
                        thumb_Bitmap = Compressor(this)
                            .setQuality(55)
                            .compressToBitmap(path)
                        val baos = ByteArrayOutputStream()
                        thumb_Bitmap?.compress(Bitmap.CompressFormat.JPEG, 55, baos)
                        val finalImage = baos.toByteArray()
                        val productRef =
                            FirebaseStorage.getInstance().reference.child("uploads/$uid.jpg")
                        val uploadTask: UploadTask = productRef.putBytes(finalImage)
                        uploadTask.addOnSuccessListener {
                            imgUrl = it.toString()
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
        Glide.with(this).load(it).into(profileImage)
    }
}