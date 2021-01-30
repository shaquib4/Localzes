package com.localze.android.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.localze.android.Category
import com.localze.android.Modals.ModelCategory
import com.localze.android.R
import com.localze.android.Seller_Products
import com.google.firebase.database.*
import kotlin.random.Random

class AdapterCategory(val context: Context, val categoryList: List<ModelCategory>,val uid:String) :
    RecyclerView.Adapter<AdapterCategory.HolderCategory>() {
    class HolderCategory(view: View) : RecyclerView.ViewHolder(view) {
        val categoryCard: RelativeLayout = view.findViewById(R.id.cardCategory)
        val title: TextView = view.findViewById(R.id.txtTitle1)
        val removeCategory: ImageView = view.findViewById(R.id.imgRemoveCategory)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_single_row, parent, false)
        return HolderCategory(view)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val category_List = categoryList[position]
        val colors: List<String>
        colors = ArrayList()
        colors.add("#008000")
        colors.add("#f156")
        colors.add("#f15642")
        colors.add("#ffce44")
        val random: Random = Random
        val l1: Int = random.nextInt(3 - 0) + 0

        holder.title.text = category_List.category
        try {
            holder.categoryCard.setBackgroundColor(Color.parseColor(colors[l1]))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, Seller_Products::class.java)
            intent.putExtra("category", category_List.category)
            context.startActivity(intent)
            (context as Category).finish()
        }
        holder.removeCategory.setOnClickListener {
            deleteCategory(position,uid)

        }
    }

    private fun deleteCategory(position: Int, uid: String) {

        val category = categoryList[position].category
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("seller").child(uid).child("Categories")
        databaseReference.orderByChild("category").equalTo(category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        i.ref.removeValue()
                    }
                    Toast.makeText(context, "Category Removed Successfully", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
}