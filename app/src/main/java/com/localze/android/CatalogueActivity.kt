package com.localze.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.localze.android.Adapters.AdapterCatalogue
import com.localze.android.Modals.ModelAddProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.File
import java.io.FileOutputStream

class CatalogueActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var view: RecyclerView
    private lateinit var catalogueList: List<ModelAddProduct>
    private lateinit var btnShare: Button
    private lateinit var shopAuth: FirebaseAuth
    private val PERMISSION_REQUEST = 10
    private var permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private lateinit var context: Context
    private lateinit var adapterCatalogue: AdapterCatalogue
    private var bitmap: Bitmap? = null
    private var shopName = ""

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalogue)
        view = findViewById(R.id.recycler_Catalogue)
        btnShare = findViewById(R.id.btnShareCatalogue)
        view.layoutManager = LinearLayoutManager(this)
       catalogueList=ArrayList<ModelAddProduct>()
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        databaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                shopName = snapshot.child("shop_name").value.toString()
            }

        })
        databaseReference.child("Products").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val obj = ModelAddProduct(
                        i.child("shopId").value.toString(),
                        i.child("productId").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("productCategory").value.toString(),
                        i.child("title").value.toString(),
                        i.child("sellingPrice").value.toString(),
                        i.child("offerPrice").value.toString(),
                        i.child("unit").value.toString(),
                        i.child("quantity").value.toString(),
                        i.child("stock").value.toString()
                    )
                    (catalogueList as ArrayList<ModelAddProduct>).add(obj)
                }
                adapterCatalogue = AdapterCatalogue(this@CatalogueActivity, catalogueList)
                view.adapter = adapterCatalogue
            }
        })
        context = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(context, permissions)) {
                saveCatalogue()
            }
        } else {
            requestPermission(permissions)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun saveCatalogue() {
        btnShare.setOnClickListener {
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
            view.measure(
                View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            bitmap = Bitmap.createBitmap(view.width, view.measuredHeight, Bitmap.Config.ARGB_8888)
            val cs = Canvas(bitmap!!)
            view.draw(cs)
            //createImageFile(b)
            createPdf()

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
            PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()

        val page: PdfDocument.Page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        canvas.drawPaint(paint)
        bitmap = Bitmap.createScaledBitmap(bitmap!!, view.width, view.height, true)

        paint.color = Color.BLUE
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        document.finishPage(page)
        val outputStream: FileOutputStream?
        val file: File = Environment.getExternalStorageDirectory()
        val dir = File(file.absolutePath + "/Localzes")
        dir.mkdirs()
        val filename = "$shopName Catalogue.pdf"
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
                this@CatalogueActivity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this@CatalogueActivity,
                "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                this@CatalogueActivity,
                permissions,
                PERMISSION_REQUEST
            )
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

            }
        }
    }
}