package com.example.localzes.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.ManageAddress
import com.example.localzes.Modals.ModelManageAddress
import com.example.localzes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdapterManageAddress(
    val context: Context,
    private val address_manage: List<ModelManageAddress>
) : RecyclerView.Adapter<AdapterManageAddress.HolderManageAddress>() {
    class HolderManageAddress(view: View) : RecyclerView.ViewHolder(view) {
        val city: TextView = view.findViewById(R.id.txtHome)
        val address: TextView = view.findViewById(R.id.txtAddress)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderManageAddress {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.address_single_row, parent, false)
        return HolderManageAddress(
            view
        )
    }

    override fun getItemCount(): Int {
        return address_manage.size
    }

    override fun onBindViewHolder(holder: HolderManageAddress, position: Int) {
        val address_user = address_manage[position]
        val userAuth: FirebaseAuth = FirebaseAuth.getInstance()
        val user = userAuth.currentUser
        val uid = user!!.uid
        holder.city.text = address_user.city
        holder.address.text = address_user.address
        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Confirmation")
            builder.setMessage("Are you sure you want to set your current address as ${address_manage[position].address},${address_manage[position].city}")
            builder.setPositiveButton("Yes") { text, listener ->
                val headers = HashMap<String, Any>()
                headers["address"] = address_manage[position].address
                val databaseRef: DatabaseReference =
                    FirebaseDatabase.getInstance().reference.child("users").child(uid)
                        .child("current_address")
                databaseRef.updateChildren(headers).addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Delivery Address Updated Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val intent = Intent(context, ManageAddress::class.java)
                context.startActivity(intent)
            }
            builder.setNegativeButton("No") { text, listener ->
                val intent = Intent(context, ManageAddress::class.java)
                context.startActivity(intent)
            }
            builder.create().show()
        }
    }
}