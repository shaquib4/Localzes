package com.localzes.android.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.localzes.android.Modals.ModelStaffOf
import com.localzes.android.R

class AdapterInvitations(val context: Context, val invitationOf: List<ModelStaffOf>) :
    RecyclerView.Adapter<AdapterInvitations.HolderInvitationsOf>() {
    class HolderInvitationsOf(view: View) : RecyclerView.ViewHolder(view) {
        val invitationShopName: TextView = view.findViewById(R.id.shopStaffNameInvitation)
        val invitationShopOwner: TextView = view.findViewById(R.id.shopOwnerNameInvitation)
        val invitationShopMobileNumber: TextView = view.findViewById(R.id.shopOwnerNumberInvitation)
        val accept: Button = view.findViewById(R.id.acceptInvite)
        val reject: Button = view.findViewById(R.id.RejectInvite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderInvitationsOf {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.invitation_single_row, parent, false)
        return HolderInvitationsOf(view)
    }

    override fun getItemCount(): Int {
        return invitationOf.size
    }

    override fun onBindViewHolder(holder: HolderInvitationsOf, position: Int) {
        val invitationStaff = invitationOf[position]
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        holder.invitationShopOwner.text = invitationStaff.shopOwnerName
        holder.invitationShopName.text = invitationStaff.shopName
        holder.invitationShopMobileNumber.text = invitationStaff.shopMobileNumber
        val databaseReference = FirebaseDatabase.getInstance().reference.child("seller")
        holder.accept.setOnClickListener {
            val headers = HashMap<String, Any>()
            headers["invitationStatus"] = "Accepted"
            databaseReference.child(uid).child("StaffOf").child(invitationStaff.invitationUid)
                .updateChildren(headers).addOnSuccessListener {
                    databaseReference.child(invitationStaff.invitationUid).child("MyStaff")
                        .child(uid).updateChildren(headers).addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Invitation Accepted Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
        }
        holder.reject.setOnClickListener {
            val headers = HashMap<String, Any>()
            headers["invitationStatus"] = "Rejected"
            databaseReference.child(uid).child("StaffOf").orderByChild("invitationUid")
                .equalTo(invitationStaff.invitationUid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children) {
                            i.ref.removeValue()
                        }
                        databaseReference.child(invitationStaff.invitationUid).child("MyStaff")
                            .child(uid).updateChildren(headers).addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Invitation Rejected Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                })
        }
    }

}