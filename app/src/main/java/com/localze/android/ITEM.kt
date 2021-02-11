package com.localze.android

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.localze.android.Adapters.AdapterFavoriteItems
import com.localze.android.Modals.ModelAddProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_two.view.*
import util.ConnectionManager

class ITEM : Fragment() {
    lateinit var favRecyclerItem: RecyclerView
    lateinit var favItems: List<ModelAddProduct>
    lateinit var favoriteItemAdapter: AdapterFavoriteItems
    lateinit var databaseReference: DatabaseReference
    lateinit var relativeItems: RelativeLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this   fragment
        val view = inflater.inflate(R.layout.fragment_two, container, false)
        favRecyclerItem = view.findViewById(R.id.recycler_favorite_item)
        relativeItems = view.findViewById(R.id.rl_Items_Favorites)
        favRecyclerItem.layoutManager = LinearLayoutManager(activity)
        favItems = ArrayList<ModelAddProduct>()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(uid)
            .child("FavoriteItems")
        if (ConnectionManager().checkConnectivity(activity as Context)){
            view.rl_FavItem.visibility=View.VISIBLE
            view.rl_retryFavItem.visibility=View.GONE
            databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (favItems as ArrayList<ModelAddProduct>).clear()
                for (i in snapshot.children) {
                    val obj = ModelAddProduct(
                        i.child("shopId").value.toString(),
                        i.child("productId").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("productCategory").value.toString(),
                        i.child("title").value.toString(),
                        i.child("sellingPrice").value.toString(),
                        i.child("offerPrice").value.toString(),
                        i.child("unit").value.toString(),
                        i.child("quantity").value.toString(),
                        i.child("stock").value.toString()
                    )
                    (favItems as ArrayList<ModelAddProduct>).add(obj)
                }
                if (favItems.isEmpty()) {
                    favRecyclerItem.visibility = View.GONE
                } else {
                    relativeItems.visibility = View.GONE
                    favRecyclerItem.visibility = View.VISIBLE
                    try {
                        favoriteItemAdapter = AdapterFavoriteItems(activity as Context, favItems)
                        favRecyclerItem.adapter = favoriteItemAdapter
                    } catch (e: Exception) {

                    }
                }
            }
        })}else{
            view.rl_FavItem.visibility=View.GONE
            view.rl_retryFavItem.visibility=View.VISIBLE
        }
        return view
    }

}