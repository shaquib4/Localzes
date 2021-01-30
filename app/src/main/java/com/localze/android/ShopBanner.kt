package com.localze.android

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_shop_banner.*
import util.ConnectionManager
import java.io.File
import java.io.FileOutputStream

class ShopBanner : AppCompatActivity() {
    private lateinit var storeName: TextView
    private val PERMISSION_REQUEST = 10
    private lateinit var banner: RelativeLayout
    private lateinit var btnSave: Button
    private lateinit var databaseReference: DatabaseReference
    private lateinit var shopAuth: FirebaseAuth
    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var context: Context
    var shopName: String = ""
    private var bitmap: Bitmap? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_banner)
        storeName = findViewById(R.id.store_name)
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
                if (ConnectionManager().checkConnectivity(this)) {
                    rl_shopBanner.visibility = View.VISIBLE
                    rl_retryShopBanner.visibility = View.GONE
                    saveBanner()
                } else {

                    rl_shopBanner.visibility = View.GONE
                    rl_retryShopBanner.visibility = View.VISIBLE

                }
            } else {
                requestPermission(permissions)
            }
        } else {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_shopBanner.visibility = View.VISIBLE
                rl_retryShopBanner.visibility = View.GONE
                saveBanner()
            } else {

                rl_shopBanner.visibility = View.GONE
                rl_retryShopBanner.visibility = View.VISIBLE

            }
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
        Toast.makeText(this, "Your Banner is saved in Downloads", Toast.LENGTH_LONG).show()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun saveBanner() {
        btnSave.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_shopBanner.visibility = View.VISIBLE
                rl_retryShopBanner.visibility = View.GONE
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                bitmap = Bitmap.createBitmap(banner.width, banner.height, Bitmap.Config.ARGB_8888)
                val cs = Canvas(bitmap!!)
                banner.draw(cs)
                //createImageFile(b)
                createPdf()
                shareGeneratedPdf()
            } else {
                rl_shopBanner.visibility = View.GONE
                rl_retryShopBanner.visibility = View.VISIBLE
            }
        }
    }

    private fun createPdf() {
        val wm: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height: Float = displayMetrics.heightPixels.toFloat()
        val width: Float = displayMetrics.widthPixels.toFloat()
        val convertHeight = height.toInt()
        val convertWidth = width.toInt()
        val document = PdfDocument()
        val pageInfo: PdfDocument.PageInfo =
            PdfDocument.PageInfo.Builder(banner.width, banner.height, 1).create()
        val page: PdfDocument.Page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        bitmap = Bitmap.createScaledBitmap(bitmap!!, banner.width, banner.height, true)

        paint.color = Color.BLUE
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        document.finishPage(page)
        val outputStream: FileOutputStream?
        val file: File = Environment.getExternalStorageDirectory()
        val dir = File(file.absolutePath + "/Localzes")
        dir.mkdirs()
        val filename = "$shopName Banner.pdf"
        val outFile = File(dir, filename)
        try {
            outputStream = FileOutputStream(outFile)
            document.writeTo(outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        document.close()
        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show()
        // openGeneratedPdf()
    }

    private fun shareGeneratedPdf() {
        val file: File = Environment.getExternalStorageDirectory()
        val dir = File(file.absolutePath + "/Localzes" + "/$shopName Banner.pdf")
        if (dir.exists()) {
            val intentShare = Intent()
            val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                dir
            )
            intentShare.action = Intent.ACTION_SEND
            intentShare.type = "application/pdf"
            intentShare.putExtra(Intent.EXTRA_STREAM, uri)
            intentShare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intentShare, "Share the file..."))
        } else {
            Toast.makeText(this, "File doesn't exist", Toast.LENGTH_SHORT).show()
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
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
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

    override fun onBackPressed() {
        val intent = Intent(this, cardBanners::class.java)
        startActivity(intent)
        finish()
    }
}