package com.localze.android.Adapters

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.localze.android.Home_seller
import com.localze.android.Modals.ModelStaffOf
import com.localze.android.R

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
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please Wait")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.setMessage("Processing You As Staff")
            progressDialog.show()
            dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val headers = HashMap<String, Any>()
                    headers["staffOfShop"] = staff_off.invitationUid
                    headers["StoreStatus"] = "CLOSED"
                    dataRef.updateChildren(headers).addOnSuccessListener {
                        val newHeaders = HashMap<String, Any>()
                        newHeaders["status"] = "Active"
                        dataRef.child("StaffOf").child(staff_off.invitationUid)
                            .updateChildren(newHeaders).addOnSuccessListener {
                                FirebaseDatabase.getInstance().reference.child("seller")
                                    .child(staff_off.invitationUid).child("MyStaff").child(uid)
                                    .updateChildren(newHeaders).addOnSuccessListener {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            context,
                                            "Account enabled successfully",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        val intent = Intent(context, Home_seller::class.java)
                                        context.startActivity(intent)
                                    }
                            }
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
                val database = FirebaseDatabase.getInstance().reference.child("seller")
                    .child(staffOf[position].invitationUid).child("MyStaff")
                database.child(uid).removeValue().addOnSuccessListener {
                    Toast.makeText(context, "Shop Removed Successfully", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}