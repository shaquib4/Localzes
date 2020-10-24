package com.example.localzes

import android.accounts.Account
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_home.*

class Home : AppCompatActivity() {
    var userDatabase: DatabaseReference?=null
    var firebaseUser: FirebaseUser?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        progress_home.visibility= View.VISIBLE



        firebaseUser= FirebaseAuth.getInstance().currentUser
        userDatabase= FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)
        showAll()

        bottom_navHome.selectedItemId = R.id.nav_home
        bottom_navHome.setOnNavigationItemSelectedListener { item ->


            when(item.itemId){

                R.id.nav_home->{

                    return@setOnNavigationItemSelectedListener true
                }
               R.id.nav_search->{

                    startActivity(Intent(this, Search::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
                R.id.nav_cart->{

                    startActivity(Intent(this,Cart::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
               R.id.nav_account->{

                    startActivity(Intent(this, Accounts::class.java))
                    overridePendingTransition(0,0)
                    finish()

                }
            }
            return@setOnNavigationItemSelectedListener false
        }
    }
    private fun showAll() {
        userDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    progress_home.visibility=View.GONE
                    val user:ModelClass?=snapshot.getValue(ModelClass::class.java)
                    user_email.text=user!!.getEmail()
                    user_name.text=user!!.getName()
                    user_phone.text=user!!.getPhone()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }



        })
    }
}