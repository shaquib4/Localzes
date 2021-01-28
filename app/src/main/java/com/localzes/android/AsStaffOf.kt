package com.localzes.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.localzes.android.Adapters.AdapterInvitations
import com.localzes.android.Adapters.AdapterStaffOf
import com.localzes.android.Modals.ModelStaffOf
import kotlinx.android.synthetic.main.activity_as_staff_of.*

class AsStaffOf : AppCompatActivity() {
    private lateinit var asStaffOf: RecyclerView
    private lateinit var staffOf: List<ModelStaffOf>
    private lateinit var invitations: List<ModelStaffOf>
    private lateinit var auth: FirebaseAuth
    private lateinit var rvInvitations: RecyclerView
    private lateinit var adapterStaff: AdapterStaffOf
    private lateinit var adapterInvitation: AdapterInvitations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_as_staff_of)
        asStaffOf = findViewById(R.id.recycler_staff)
        rvInvitations = findViewById(R.id.recyclerInvitatio)
        staffOf = ArrayList<ModelStaffOf>()
        invitations = ArrayList<ModelStaffOf>()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        asStaffOf.layoutManager = LinearLayoutManager(this)
        rvInvitations.layoutManager = LinearLayoutManager(this)
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("StaffOf")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (staffOf as ArrayList<ModelStaffOf>).clear()
                for (i in snapshot.children) {
                    val obj = ModelStaffOf(
                        i.child("shopOwnerName").value.toString(),
                        i.child("shopName").value.toString(),
                        i.child("shopMobileNumber").value.toString(),
                        i.child("status").value.toString(),
                        i.child("invitationUid").value.toString(),
                        i.child("invitationStatus").value.toString()
                    )
                    if (i.child("invitationStatus").value.toString() == "Accepted") {
                        (staffOf as ArrayList<ModelStaffOf>).add(obj)
                    }
                }
                adapterStaff = AdapterStaffOf(this@AsStaffOf, staffOf)
                asStaffOf.adapter = adapterStaff
            }
        })
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (invitations as ArrayList<ModelStaffOf>).clear()
                for (i in snapshot.children) {
                    val obj = ModelStaffOf(
                        i.child("shopOwnerName").value.toString(),
                        i.child("shopName").value.toString(),
                        i.child("shopMobileNumber").value.toString(),
                        i.child("status").value.toString(),
                        i.child("invitationUid").value.toString(),
                        i.child("invitationStatus").value.toString()
                    )
                    if (i.child("invitationStatus").value.toString() == "") {
                        (invitations as ArrayList<ModelStaffOf>).add(obj)
                    }
                }
                adapterInvitation = AdapterInvitations(this@AsStaffOf, invitations)
                rvInvitations.adapter = adapterInvitation

            }

        })
    }
}