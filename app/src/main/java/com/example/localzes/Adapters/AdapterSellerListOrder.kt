package com.example.localzes.Adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Modals.ModelList
import com.example.localzes.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdapterSellerListOrder(
    val context: Context,
    val seller_order_list: List<ModelList>,
    val orderId:String,
    val orderBy:String
) :
    RecyclerView.Adapter<AdapterSellerListOrder.HolderSellerListOrder>() {
    class HolderSellerListOrder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSNo: TextView = view.findViewById(R.id.txtNo)
        val txtItem_Name: TextView = view.findViewById(R.id.txtItem_Name)
        val txtQuan: TextView = view.findViewById(R.id.txtQuan)
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
        holder.txtItem_Name.text=sellerOrderList.itemName
        holder.txtQuan.text=sellerOrderList.itemQuantity


        holder.itemRemove.setOnClickListener {
            val uAuth = FirebaseAuth.getInstance()
            val user = uAuth.currentUser
            val uid = user!!.uid
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists").child(orderId)
                    .child("ListItems").child(sellerOrderList.itemId)
            val headers = HashMap<String, Any>()
            headers["itemCost"] = "0"
            databaseReference.updateChildren(headers)
            val userDatabase:DatabaseReference=FirebaseDatabase.getInstance().reference.child("users").child(orderBy)
                .child("MyOrderList").child(orderId).child("ListItems").child(sellerOrderList.itemId)
            val userMap = HashMap<String, Any>()
            userMap["itemCost"] = "0"
            userDatabase.updateChildren(userMap)

            holder.itemRemove.visibility=View.GONE
            holder.itemRevive.visibility=View.VISIBLE

        }
        holder.itemRevive.setOnClickListener {
            val uAuth = FirebaseAuth.getInstance()
            val user = uAuth.currentUser
            val uid = user!!.uid
            val databaseReference: DatabaseReference =
                FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists").child(orderId)
                    .child("ListItems").child(sellerOrderList.itemId)
            val headers = HashMap<String, Any>()
            headers["itemCost"] = ""
            databaseReference.updateChildren(headers)
            val userDatabase:DatabaseReference=FirebaseDatabase.getInstance().reference.child("users").child(orderBy)
                .child("MyOrderList").child(orderId).child("ListItems").child(sellerOrderList.itemId)
            val userMap = HashMap<String, Any>()
            userMap["itemCost"] = ""
            userDatabase.updateChildren(userMap)
            holder.itemRemove.visibility=View.VISIBLE
            holder.itemRevive.visibility=View.GONE
            holder.edtPrice.setText("")
        }
        val uAuth = FirebaseAuth.getInstance()
        val user = uAuth.currentUser
        val uid = user!!.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists").child(orderId)
                .child("ListItems").child(sellerOrderList.itemId)
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                holder.edtPrice.isEnabled=true
                val itemCost=snapshot.child("itemCost").value.toString()

                if (itemCost=="0"){

                    holder.edtPrice.isEnabled=false
                    holder.edtPrice.setText("0")

                }else{
                    holder.edtPrice.addTextChangedListener(object :TextWatcher{
                        override fun afterTextChanged(s: Editable?) {

                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            priceEdit(s.toString(),sellerOrderList.itemId)
                        }
                    })
                }
            }
        })
        val databaseReferences: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists").child(orderId)
                .child("ListItems").child(sellerOrderList.itemId)
        databaseReferences.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                holder.edtPrice.isEnabled=true
                val itemCost=snapshot.child("itemCost").value.toString()

                if (itemCost=="0"){
                    holder.edtPrice.isEnabled=false
                    holder.itemRemove.visibility=View.GONE
                    holder.itemRevive.visibility=View.VISIBLE
                    holder.edtPrice.setText("0")

                }else{
                    holder.edtPrice.setText(sellerOrderList.itemCost)
                    holder.itemRemove.visibility=View.VISIBLE
                    holder.itemRevive.visibility=View.GONE
                    holder.edtPrice.addTextChangedListener(object :TextWatcher{
                        override fun afterTextChanged(s: Editable?) {

                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            priceEdit(s.toString(),sellerOrderList.itemId)
                        }
                    })
                }
            }
        })

    }

    private fun priceEdit(s: String, itemId: String) {
        val uAuth = FirebaseAuth.getInstance()
        val user = uAuth.currentUser
        val uid = user!!.uid
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("OrdersLists").child(orderId)
                .child("ListItems").child(itemId)
        val headers = HashMap<String, Any>()
        headers["itemCost"] = s
        databaseReference.updateChildren(headers)
        val userDatabase:DatabaseReference=FirebaseDatabase.getInstance().reference.child("users").child(orderBy)
            .child("MyOrderList").child(orderId).child("ListItems").child(itemId)
        val userMap = HashMap<String, Any>()
        userMap["itemCost"] = s
        userDatabase.updateChildren(userMap)
    }
}