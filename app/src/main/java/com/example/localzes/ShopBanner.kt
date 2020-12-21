package com.example.localzes

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ShopBanner : AppCompatActivity() {
    private lateinit var storeName: TextView
    private val PERMISSION_REQUEST = 10
    private lateinit var banner: RelativeLayout
    private lateinit var btnSave: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var shopAuth: FirebaseAuth
    private var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private lateinit var context: Context
    var shopName: String = ""

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_banner)
        storeName = findViewById(R.id.store_Name)
        btnSave = findViewById(R.id.saveBanner)
        banner = findViewById(R.id.banner)
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                shopName = snapshot.child("shop_name").value.toString()
                storeName.text = shopName
            }

        })
        context = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(context, permissions)) {
                saveBanner()
            } else {
                requestPermission(permissions)
            }
        } else {
            saveBanner()
        }
    }

    private fun createImageFile(b: Bitmap?) {
        /*val bytes = ByteArrayOutputStream()
        b?.compress(Bitmap.CompressFormat.PNG, 50, bytes)
        val filePath =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "$storeName Banner.png"
        val f = File(filePath)
        f.createNewFile()
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        fo.close()
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show()*/
        var outputStream: FileOutputStream? = null
        val file: File = Environment.getExternalStorageDirectory()
        val dir = File(file.absolutePath + "/Localzes")
        dir.mkdirs()
        val filename = "$shopName Banner.png"
        val outFile = File(dir, filename)
        try {
            outputStream = FileOutputStream(outFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        b?.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
        try {
            outputStream?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            outputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun saveBanner() {
        btnSave.setOnClickListener {
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            val b = Bitmap.createBitmap(banner.width, banner.height, Bitmap.Config.ARGB_8888)
            val cs = Canvas(b)
            banner.draw(cs)
            createImageFile(b)
        }
    }

    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED) {
                allSuccess = false
            }
        }
        return allSuccess
    }

    fun requestPermission(permissions: Array<out String>) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@ShopBanner,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this@ShopBanner,
                "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(this@ShopBanner, permissions, PERMISSION_REQUEST)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    var requestAgain =
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                            permissions[i]
                        )
                    if (requestAgain) {
                        Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Go to settings and enable the permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            if (allSuccess) {
                saveBanner()
            }
        }
    }
}