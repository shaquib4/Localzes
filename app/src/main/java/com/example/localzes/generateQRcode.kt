package com.example.localzes

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.activity_generate_q_rcode.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class generateQRcode : AppCompatActivity() {
    private val PERMISSION_REQUEST = 10

    private lateinit var storeQr: RelativeLayout
    private var permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private lateinit var context: Context
    private lateinit var shopAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storeName: TextView
    var shopName: String = ""
    var shopId: String = ""

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_q_rcode)
        storeName = findViewById(R.id.store_Name)
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        shopId = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("seller").child(shopId)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                shopName = snapshot.child("shop_name").value.toString()
                storeName.text = shopName
            }

        })
        context = this
        storeQr = findViewById(R.id.storeQR)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(context, permissions)) {
                generateQRCode()
            } else {
                requestPermission(permissions)
            }
        } else {
            generateQRCode()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, cardBanners::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun generateQRCode() {
        val bitmap = encodeAsBitmap(shopId, 400, 400, context)
        iv_qr_code.setImageBitmap(bitmap)

        qrsave.setOnClickListener {
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            val b = Bitmap.createBitmap(storeQr.width, storeQr.height, Bitmap.Config.ARGB_8888)
            val cs = Canvas(b)
            storeQr.draw(cs)


            createImageFile(b)
            // val string="name,line".split(",").toTypedArray()

        }
    }

    @SuppressLint("ResourceAsColor")
    private fun bit(view: View): Bitmap? {

        val rBitmap = Bitmap.createBitmap(storeQr.getWidth(), 300, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(rBitmap)
        val bgDraw = view.background
        if (bgDraw != null) {
            bgDraw.draw(canvas)
        } else {
            canvas.drawColor(android.R.color.background_dark)
        }
        view.draw(canvas)
        return rBitmap

    }


    fun createImageFile(bitmapScaled: Bitmap?) {
        val bytes = ByteArrayOutputStream()
        bitmapScaled?.compress(Bitmap.CompressFormat.PNG, 40, bytes)
        val filepath =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "$shopName.png"
        val f = File(filepath)
        f.createNewFile()
        val fo = FileOutputStream(f)
        fo.write(bytes.toByteArray())
        fo.close()
        Toast.makeText(this, "saved", Toast.LENGTH_LONG).show()
    }

    fun encodeAsBitmap(str: String, WIDTH: Int, HEIGHT: Int, ctx: Context): Bitmap? {
        val result: BitMatrix
        try {

            result = MultiFormatWriter().encode(
                str,
                BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null
            )
        } catch (iae: IllegalArgumentException) {
            return null
        }
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result.get(x, y)) -0x1000000 else -0x1
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
//        createImageFile(bitmap)
        return bitmap
    }

    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    fun requestPermission(permissions: Array<out String>) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this@generateQRcode,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this@generateQRcode,
                "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.",
                Toast.LENGTH_LONG
            ).show();
        } else {
            ActivityCompat.requestPermissions(this@generateQRcode, permissions, PERMISSION_REQUEST);
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
                generateQRCode()
            }
        }
    }
}