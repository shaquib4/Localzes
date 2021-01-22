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
import com.localzes.android.Modals.ModelStaffOf
import com.localzes.android.Modals.Upload
import com.localzes.android.R

class AdapterStaffOf(val context: Context, val staffOf: List<ModelStaffOf>) :
    RecyclerView.Adapter<AdapterStaffOf.HolderStaffOf>() {
    class HolderStaffOf(view: View) : RecyclerView.ViewHolder(view) {
        val staffShopName: TextView = view.findViewById(R.id.shopStaffName)
        val shopOwnerName: TextView = view.findViewById(R.id.shopOwnerName)
        val shopOwnerPhone: TextView = view.findViewById(R.id.shopOwnerNumber)
        val deleteShop: ImageView = view.findViewById(R.id.imgStaffDelete)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderStaffOf {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.staff_off, parent, false)
        return HolderStaffOf(view)
    }

    override fun getItemCount(): Int {
        return staffOf.size
    }

    override fun onBindViewHolder(holder: HolderStaffOf, position: Int) {
        val staff_off = staffOf[position]
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        holder.staffShopName.text = staff_off.shopName
        holder.shopOwnerName.text = staff_off.shopOwnerName
        holder.shopOwnerPhone.text = staff_off.shopMobileNumber
        val dataRef = FirebaseDatabase.getInstance().reference.child("seller").child(uid)
        holder.itemView.setOnClickListener {
            dataRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val headers = HashMap<String, Any>()
                    headers["staffOfShop"] = "${staff_off.shopName},${staff_off.shopMobileNumber}"
                    dataRef.updateChildren(headers).addOnSuccessListener {
                        Toast.makeText(context, "Account enabled successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        }
        holder.deleteShop.setOnClickListener {
            deleteStaffShop(position)
        }
    }

    private fun deleteStaffShop(position: Int) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        val dataReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("StaffOf")
                .orderByChild("shopMobileNumber").equalTo(staffOf[position].shopMobileNumber)
        dataReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {


            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    i.ref.removeValue()
                }
                Toast.makeText(context, "Shop Removed Successfully", Toast.LENGTH_SHORT).show()
            }

        })
    }
}