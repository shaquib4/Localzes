package com.example.localzes


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*

class ManageAddress : AppCompatActivity() {
    private lateinit var userDatabase:DatabaseReference
    private lateinit var auth:FirebaseAuth
    private lateinit var addresses:List<ModelManageAddress>
    private lateinit var recyclerManageAddress: RecyclerView
    private lateinit var userAddressAdapter:AdapterManageAddress
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_address)
        auth= FirebaseAuth.getInstance()
        val user=auth.currentUser
        val uid=user!!.uid
       recyclerManageAddress=findViewById(R.id.recycler_Address)
        recyclerManageAddress.layoutManager=LinearLayoutManager(this)

        userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(uid).child("address")
        addresses=ArrayList<ModelManageAddress>()

        userDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                (addresses as ArrayList<ModelManageAddress>).clear()
                for (i in snapshot.children) {

                    val obj =ModelManageAddress(
                        i.child("address").value.toString(),
                        i.child("city").value.toString(),
                        i.child("pinCode").value.toString(),
                        i.child("country").value.toString(),
                        i.child("state").value.toString()

                    )
                    (addresses as ArrayList<ModelManageAddress>).add(obj)
                    progress_home.visibility= View.GONE
                }
                userAddressAdapter= AdapterManageAddress(this@ManageAddress,addresses)
                //recyclerShopUser.adapter=userShopAdapter
            }

        })
    }
}