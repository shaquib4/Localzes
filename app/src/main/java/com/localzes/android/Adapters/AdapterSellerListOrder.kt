package com.localzes.android.Adapters

import android.content.Context
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.localzes.android.Modals.ModelList
import com.localzes.android.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdapterSellerListOrder(
    val context: Context,
    val seller_order_list: List<ModelList>,
    val orderId: String,
    val orderBy: String,
    val orderStatus: String
) :
    RecyclerView.Adapter<AdapterSellerListOrder.HolderSellerListOrder>() {
    class HolderSellerListOrder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSNo: TextView = view.findViewById(R.id.txtNo)
        val txtItem_Name: TextView = view.findViewById(R.id.txtItem_Name)

        val edtPrice: EditText = view.findViewById(R.id.edtPrice)
        val itemRemove: ImageView = view.findViewById(R.id.itemRemove)
        val itemRevive: ImageView = view.findViewById(R.id.itemRevive)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSellerListOrder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_single_row_seller_order, parent, false)
        return HolderSellerListOrder(view)
    }

    override fun getItemCount(): Int {
        return seller_order_list.size

    }

    override fun onBindViewHolder(holder: HolderSellerListOrder, position: Int) {
        val sellerOrderList = seller_order_list[position]
        holder.txtSNo.text = (position + 1).toString() + "."
        holder.txtItem_Name.text = "${sellerOrderList.itemName}X${sellerOrderList.itemQuantity}"
        val mString = "${sellerOrderList.itemName}X${sellerOrderList.itemQuantity}"
        val spannableString = SpannableString(mString)
        val mStrikeThrough = StrikethroughSpan()
        spannableString.setSpan(mStrikeThrough, 0, mString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val uAuths = FirebaseAuth.getInstance()
        val users = uAuths.currentUser
        val uids = users!!.uid
        if (orderStatus != "Pending") {
            holder.edtPrice.setText(sellerOrderList.itemCost)
            holder.edtPrice.isEnabled = false
            holder.itemRevive.visibility = View.GONE
            holder.itemRemove.visibility = View.GONE
        } else {
            val uAuth = FirebaseAuth.getInstance()
            val user = uAuth.currentUser
            val uid = user!!.uid

            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                    .child("OrdersLists").child(orderId)
                    .child("ListItems").child(sellerOrderList.itemId)
            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    val itemCost = snapshot.child("itemCost").value.toString()

                    if (itemCost == "0") {

                        holder.edtPrice.isEnabled = false
                        holder.edtPrice.setText("0")

                    } else {
                        holder.edtPrice.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {

                            }

                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {

                            }

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                priceEdit(s.toString(), sellerOrderList.itemId)
                            }
                        })
                    }
                }
            })
            val databaseReferences: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                    .child("OrdersLists").child(orderId)
                    .child("ListItems").child(sellerOrderList.itemId)
            databaseReferences.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    val itemCost = snapshot.child("itemCost").value.toString()

                    if (itemCost == "0") {
                        holder.edtPrice.setText("0")
                        holder.edtPrice.isEnabled = false
                        holder.itemRemove.visibility = View.GONE
                        holder.itemRevive.visibility = View.VISIBLE


                    } else {
                        holder.edtPrice.setText(sellerOrderList.itemCost)
                        holder.itemRemove.visibility = View.VISIBLE
                        holder.itemRevive.visibility = View.GONE
                        holder.edtPrice.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {

                            }

                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {

                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                                priceEdit(s.toString(), sellerOrderList.itemId)
                            }
                        })
                    }
                }
            })
        }

        holder.itemRemove.setOnClickListener {
            holder.txtItem_Name.text = spannableString
            val uAuth = FirebaseAuth.getInstance()
            val user = uAuth.currentUser
            val uid = user!!.uid
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                    .child("OrdersLists").child(orderId)
                    .child("ListItems").child(sellerOrderList.itemId)
            val headers = HashMap<String, Any>()
            headers["itemCost"] = "0"
            databaseReference.updateChildren(headers)
            val userDatabase: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("users").child(orderBy)
                    .child("MyOrderList").child(orderId).child("ListItems")
                    .child(sellerOrderList.itemId)
            val userMap = HashMap<String, Any>()
            userMap["itemCost"] = "0"
            userDatabase.updateChildren(userMap)

            holder.itemRemove.visibility = View.GONE
            holder.itemRevive.visibility = View.VISIBLE
            holder.edtPrice.setText("0")
            holder.edtPrice.isEnabled = false

        }
        holder.itemRevive.setOnClickListener {
            holder.txtItem_Name.text = "${sellerOrderList.itemName}  X ${sellerOrderList.itemQuantity}"
            val uAuth = FirebaseAuth.getInstance()
            val user = uAuth.currentUser
            val uid = user!!.uid
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("seller").child(uid)
                    .child("OrdersLists").child(orderId)
                    .child("ListItems").child(sellerOrderList.itemId)
            val headers = HashMap<String, Any>()
            headers["itemCost"] = ""
            databaseReference.updateChildren(headers)
            val userDatabase: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("users").child(orderBy)
                    .child("MyOrderList").child(orderId).child("ListItems")
                    .child(sellerOrderList.itemId)
            val userMap = HashMap<String, Any>()
            userMap["itemCost"] = ""
            userDatabase.updateChildren(userMap)
            holder.itemRemove.visibility = View.VISIBLE
            holder.itemRevive.visibility = View.GONE
            holder.edtPrice.setText("")
            holder.edtPrice.isEnabled = true
        }


    }

    private fun priceEdit(s: String, itemId: String) {
        val uAuth = FirebaseAuth.getInstance()
        val user = uAuth.currentUser
        val uid = user!!.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists")
                .child(orderId)
                .child("ListItems").child(itemId)
        val headers = HashMap<String, Any>()
        headers["itemCost"] = s
        databaseReference.updateChildren(headers)
        val userDatabase: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(orderBy)
                .child("MyOrderList").child(orderId).child("ListItems").child(itemId)
        val userMap = HashMap<String, Any>()
        userMap["itemCost"] = s
        userDatabase.updateChildren(userMap)
    }
}