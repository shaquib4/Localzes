package com.localzes.android.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.localzes.android.Modals.ModalAddStaff
import com.localzes.android.R

class AdapterMyStaff(val context: Context, val staffDetails: List<ModalAddStaff>) :
    RecyclerView.Adapter<AdapterMyStaff.HolderMyStaff>() {
    class HolderMyStaff(view: View) : RecyclerView.ViewHolder(view) {
        val staffName: TextView = view.findViewById(R.id.StaffName)
        val staffNumber: TextView = view.findViewById(R.id.StaffNumber)
        val staffAccess: TextView = view.findViewById(R.id.StaffAccess)
        val imgDelete: ImageView = view.findViewById(R.id.imgDelete)
        val imgEdit: ImageView = view.findViewById(R.id.imgEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderMyStaff {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.staff_single_row, parent, false)
        return HolderMyStaff(view)

    }

    override fun getItemCount(): Int {
        return staffDetails.size
    }

    override fun onBindViewHolder(holder: HolderMyStaff, position: Int) {
        val staff_details = staffDetails[position]

        holder.staffName.text = staff_details.name
        holder.staffNumber.text = staff_details.phone
        holder.staffAccess.text = staff_details.access
        holder.imgDelete.setOnClickListener {
            deleteStaff(position)
        }
        holder.imgEdit.setOnClickListener {

        }
    }

    private fun deleteStaff(position: Int) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        val staffUid = staffDetails[position].uid
        val databaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("MyStaff")
                .orderByChild("uid").equalTo(staffUid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    i.ref.removeValue()
                }
                Toast.makeText(context, "Staff Removed Successfully", Toast.LENGTH_SHORT).show()
            }

        })


    }
}