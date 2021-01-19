package com.example.localzes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.localzes.Adapters.AdapterFavoriteShops
import com.example.localzes.Modals.Upload
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_one.*
import kotlinx.android.synthetic.main.fragment_one.view.*
import util.ConnectionManager


class SHOP : Fragment() {
    lateinit var favRecyclerShop: RecyclerView
    lateinit var favShops: List<Upload>
    lateinit var relativeShop: RelativeLayout
    lateinit var favoriteAdapter: AdapterFavoriteShops
    lateinit var databaseReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_one, container, false)
        favRecyclerShop = view.findViewById(R.id.recycler_favorite_shop)
        relativeShop = view.findViewById(R.id.rl_Shop_Favorites)
        favShops = ArrayList<Upload>()
        favRecyclerShop.layoutManager = LinearLayoutManager(activity)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        databaseReference =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("Favorites")
        if (ConnectionManager().checkConnectivity(activity as Context)){
            view.rl_FavShop.visibility=View.VISIBLE
           view.rl_retryFavShop.visibility=View.GONE
            databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (favShops as ArrayList<Upload>).clear()
                for (i in snapshot.children) {
                    val obj = Upload(
                        i.child("shopId").value.toString(),
                        i.child("phone").value.toString(),
                        i.child("name").value.toString(),
                        i.child("email").value.toString(),
                        i.child("address").value.toString(),
                        i.child("shop_name").value.toString(),
                        i.child("imageUrl").value.toString(),
                        i.child("category1").value.toString(),
                        i.child("upi").value.toString(),
                        i.child("locality").value.toString(),
                        i.child("city").value.toString(),
                        i.child("pinCode").value.toString(),
                        i.child("state").value.toString(),
                        i.child("country").value.toString(),
                        i.child("openingTime").value.toString(),
                        i.child("closingTime").value.toString(),
                        i.child("closingDay").value.toString(),
                        i.child("locality2").value.toString()
                    )
                    (favShops as ArrayList<Upload>).add(obj)
                }
                if (favShops.isEmpty()) {
                    favRecyclerShop.visibility = View.GONE
                } else {
                    relativeShop.visibility = View.GONE
                    favRecyclerShop.visibility = View.VISIBLE
                    try {
                        favoriteAdapter = AdapterFavoriteShops(activity as Context, favShops)
                        favRecyclerShop.adapter = favoriteAdapter
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })}else{
           view.rl_FavShop.visibility=View.GONE
           view.rl_retryFavShop.visibility=View.VISIBLE
        }
        return view
    }

}