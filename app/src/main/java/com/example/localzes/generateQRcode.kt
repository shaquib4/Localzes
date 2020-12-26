package com.example.localzes

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.android.synthetic.main.activity_generate_q_rcode.*
import util.ConnectionManager
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class generateQRcode : AppCompatActivity() {
    private val PERMISSION_REQUEST = 10

    private lateinit var storeQr: RelativeLayout
    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var context: Context
    private lateinit var shopAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storeName: TextView
    var shopName: String = ""
    var shopId: String = ""
    private var bitmapN: Bitmap? = null

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
                if (ConnectionManager().checkConnectivity(this)) {
                    rl_Qr.visibility = View.VISIBLE
                    rl_retryQr.visibility = View.GONE
                    generateQRCode()
                } else {
                    rl_Qr.visibility = View.GONE
                    rl_retryQr.visibility = View.VISIBLE
                }
            } else {
                requestPermission(permissions)
            }
        } else {
            if (ConnectionManager().checkConnectivity(this)) {
                rl_Qr.visibility = View.VISIBLE
                rl_retryQr.visibility = View.GONE
                generateQRCode()
            } else {
                rl_Qr.visibility = View.GONE
                rl_retryQr.visibility = View.VISIBLE
            }
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
            if (ConnectionManager().checkConnectivity(this)) {
                rl_Qr.visibility = View.VISIBLE
                rl_retryQr.visibility = View.GONE
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                bitmapN =
                    Bitmap.createBitmap(storeQr.width, storeQr.height, Bitmap.Config.ARGB_8888)
                val cs = Canvas(bitmapN!!)
                storeQr.draw(cs)
                createPdf()
                shareGeneratedPdf()
            } else {
                rl_Qr.visibility = View.GONE
                rl_retryQr.visibility = View.VISIBLE
            }
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

    private fun createPdf() {
        val wm: WindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(storeQr.width, storeQr.height, 1).create()
        val page: PdfDocument.Page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        bitmapN = Bitmap.createScaledBitmap(bitmapN!!, storeQr.width, storeQr.height, true)
        paint.color = Color.BLUE
        canvas.drawBitmap(bitmapN!!, 0f, 0f, null)
        document.finishPage(page)
        val file: File = Environment.getExternalStorageDirectory()
        val dir = File(file.absolutePath + "/Localzes")
        dir.mkdirs()
        val outputStream: FileOutputStream?
        val fileName = "$shopName QRCode.pdf"
        val outfile = File(dir, fileName)
        try {
            outputStream = FileOutputStream(outfile)
            document.writeTo(outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
        document.close()
        Toast.makeText(this, "PDF is created!!!", Toast.LENGTH_SHORT).show()
    }

    private fun shareGeneratedPdf() {
        val file: File = Environment.getExternalStorageDirectory()
        val dir = File(file.absolutePath + "/Localzes" + "/$shopName QRCode.pdf")
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
            ActivityCompat.requestPermissions(this@generateQRcode, permissions, PERMISSION_REQUEST)
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
                generateQRCode()
            }
        }
    }
}