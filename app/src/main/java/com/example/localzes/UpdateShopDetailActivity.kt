package com.example.localzes

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UpdateShopDetailActivity : AppCompatActivity() {
    private lateinit var imagePathUpdated: Uri
    private lateinit var databaseRef: DatabaseReference
    private lateinit var updateAuth: FirebaseAuth
    private lateinit var imageShopUpdate: ImageView
    private lateinit var shopCategoryUpdate: Spinner
    private lateinit var shopNameUpdate: EditText
    private lateinit var upiIdUpdate: EditText
    private lateinit var btnUpdateDetails: Button
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

            val user = FirebaseAuth.getInstance().currentUser

            val uid = user!!.uid
            val shopRef =
                FirebaseStorage.getInstance().reference.child(   "uploads/" + uid
                        + ".jpg")
            shopRef.putFile(imagePathUpdated).addOnSuccessListener {
                shopRef.downloadUrl.addOnSuccessListener {
                    val imageUrl: Uri = it
                 val request=   UserProfileChangeRequest.Builder().setPhotoUri(it).build()
                   user.updateProfile(request).addOnSuccessListener {
                       val headers = HashMap<String, Any>()
                       headers["imageUrl"]=imageUrl.toString()
                       headers["category1"] = shopCategoryUpdate.selectedItem.toString().trim()
                       headers["shop_name"] = shopNameUpdate.text.toString().trim()
                       headers["upi"] = upiIdUpdate.text.toString().trim()
                       databaseRef.updateChildren(headers).addOnSuccessListener {
                           Toast.makeText(this, "Shop Updated Successfully", Toast.LENGTH_SHORT).show()
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
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            imagePathUpdated = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imagePathUpdated)
            imageShopUpdate.setImageBitmap(bitmap)
        }
    }
}