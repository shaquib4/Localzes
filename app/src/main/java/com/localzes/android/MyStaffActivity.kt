package com.localzes.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.localzes.android.Adapters.AdapterMyStaff
import com.localzes.android.Modals.ModalAddStaff

class MyStaffActivity : AppCompatActivity() {
    private lateinit var addStaff: FloatingActionButton
    private lateinit var recyclerAddStaff: RecyclerView
    private lateinit var adapterStaffs: AdapterMyStaff
    private lateinit var shopAuth: FirebaseAuth
    private lateinit var staffDetails: List<ModalAddStaff>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_staff)
        addStaff = findViewById(R.id.addStaff)
        shopAuth = FirebaseAuth.getInstance()
        val user = shopAuth.currentUser
        val uid = user!!.uid
        recyclerAddStaff = findViewById(R.id.recycler_add_staff)
        staffDetails = ArrayList<ModalAddStaff>()
        recyclerAddStaff.layoutManager = LinearLayoutManager(this)
        addStaff.setOnClickListener {
            val intent = Intent(this, AddStaff::class.java)
            startActivity(intent)
            finish()
        }
        val dataReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("MyStaff")
        dataReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (staffDetails as ArrayList<ModalAddStaff>).clear()
                for (i in snapshot.children) {
                    val obj = ModalAddStaff(
                        i.child("name").value.toString(),
                        i.child("phone").value.toString(),
                        i.child("address").value.toString(),
                        i.child("status").value.toString(),
                        i.child("access").value.toString(),
                        i.child("uid").value.toString(),
                        i.child("invitationStatus").value.toString()
                    )
                    if (i.child("invitationStatus").value.toString() == "Accepted") {
                        (staffDetails as ArrayList<ModalAddStaff>).add(obj)
                    }
                }
                adapterStaffs = AdapterMyStaff(this@MyStaffActivity, staffDetails)
                recyclerAddStaff.adapter = adapterStaffs
            }
        })
    }
}