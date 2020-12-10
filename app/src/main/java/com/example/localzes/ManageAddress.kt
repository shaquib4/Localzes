package com.example.localzes


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterManageAddress
import com.example.localzes.Modals.ModelManageAddress
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManageAddress : AppCompatActivity() {
    private lateinit var userDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var addresses: List<ModelManageAddress>
    private lateinit var recyclerManageAddress: RecyclerView
    private lateinit var userAddressAdapter: AdapterManageAddress
    private lateinit var txtCurrentAddress: TextView
    private lateinit var imgBackManage: ImageView
    private lateinit var addNewAddress: Button
    private lateinit var mobileTv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_address)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        recyclerManageAddress = findViewById(R.id.recycler_Address)
        txtCurrentAddress = findViewById(R.id.txtAddress)
        addNewAddress = findViewById(R.id.btnAddNewAddress)
        imgBackManage = findViewById(R.id.imgBackManage)
        mobileTv = findViewById(R.id.txtMobile)
        recyclerManageAddress.layoutManager = LinearLayoutManager(this)

        userDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid)
        addresses = ArrayList<ModelManageAddress>()

        userDatabase.child("address").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (addresses as ArrayList<ModelManageAddress>).clear()
                for (i in snapshot.children) {

                    val obj = ModelManageAddress(
                        i.child("address").value.toString(),
                        i.child("city").value.toString(),
                        i.child("pinCode").value.toString(),
                        i.child("country").value.toString(),
                        i.child("state").value.toString()

                    )
                    (addresses as ArrayList<ModelManageAddress>).add(obj)

                }
                userAddressAdapter =
                    AdapterManageAddress(
                        this@ManageAddress,
                        addresses
                    )
                recyclerManageAddress.adapter = userAddressAdapter
            }

        })
        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val mobNo = snapshot.child("phone").value.toString()
                mobileTv.text = mobNo

            }

        })
        userDatabase.child("current_address").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val currentAddress = snapshot.child("address").value.toString()
                txtCurrentAddress.text = currentAddress
            }
        })
        imgBackManage.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
        addNewAddress.setOnClickListener {

        }
    }
}