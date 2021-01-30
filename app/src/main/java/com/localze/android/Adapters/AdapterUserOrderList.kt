package com.localze.android.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.localze.android.Modals.ModelList
import com.localze.android.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdapterUserOrderList(
    val context: Context,
    private val user_order_List: List<ModelList>,
    val orderId: String
) :
    RecyclerView.Adapter<AdapterUserOrderList.HolderUserOrderList>() {
    class HolderUserOrderList(view: View) : RecyclerView.ViewHolder(view) {
        val txtNoCustomer: TextView = view.findViewById(R.id.txtNoCustomer)
        val txtItem_NameCustomer: TextView = view.findViewById(R.id.txtItem_NameCustomer)
        val txtQuanCustomer: TextView = view.findViewById(R.id.txtQuanCustomer)
        val txtPriceCustomer: TextView = view.findViewById(R.id.txtPriceCustomer)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderUserOrderList {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_single_row_customer_order, parent, false)
        return HolderUserOrderList(view)

    }

    override fun getItemCount(): Int {
        return user_order_List.size
    }

    override fun onBindViewHolder(holder: HolderUserOrderList, position: Int) {
        val userOrderList = user_order_List[position]
        holder.txtNoCustomer.text = (position + 1).toString() + "."
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("MyOrderList")
                .child(orderId)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("listStatus").value.toString() == "Confirm") {
                    if (userOrderList.itemCost == 0.toString()) {
                        holder.txtPriceCustomer.text = "Not Available"
                    } else {
                        holder.txtPriceCustomer.text = "₹"+userOrderList.itemCost
                    }
                } else {
                    holder.txtPriceCustomer.visibility = View.GONE
                }
            }

        })
        holder.txtItem_NameCustomer.text = userOrderList.itemName
        holder.txtQuanCustomer.text = "X" + userOrderList.itemQuantity
    }
}