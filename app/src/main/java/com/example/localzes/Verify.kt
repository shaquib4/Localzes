package com.example.localzes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_verify.*
import util.ConnectionManager
import java.util.HashMap
import java.util.concurrent.TimeUnit

class Verify : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var lastDigit: TextView
    var userDatabase: DatabaseReference? = null
    lateinit var storedVerificationId: String
    private lateinit var suserDatabase: DatabaseReference
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    var firebaseUser: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify)
        auth = FirebaseAuth.getInstance()
        lastDigit = findViewById(R.id.lastNumber)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val phone = intent.getStringExtra("phone")
        val number = phone.toString()
        lastDigit.text = number
        retryOTP.setOnClickListener {
            this.recreate()
        }


        sendVerification("+91$phone")
        progress_verify.visibility = View.VISIBLE


    }

    private fun sendVerification(phone: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone,
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            TaskExecutors.MAIN_THREAD,
            callbacks
        )
    }

    var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            val code = credential.smsCode
            if (code != null) {

                verifyVerification(code)
                otp.setText(code)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(applicationContext, "" + e.message, Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(applicationContext, "" + e.message, Toast.LENGTH_SHORT).show()
            }

        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {


            storedVerificationId = verificationId
            resendToken = token


        }
    }


    private fun verifyVerification(codeUser: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, codeUser)
        signUp(credential)
    }

    private fun signUp(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    progress_verify.visibility = View.GONE
                    auth = FirebaseAuth.getInstance()
                    val users = auth.currentUser
                    var uid = users!!.uid
                    suserDatabase =
                        FirebaseDatabase.getInstance().reference.child("customers").child(uid)

                    val userMap = HashMap<String, Any>()
                    val phone = intent.getStringExtra("phone")
                    val intent = Intent(this, Continueas::class.java)
                    intent.putExtra("phone1", phone)
                    userMap["uid"] = uid
                    userMap["phone"] = phone.toString()
                    suserDatabase.setValue(userMap).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                baseContext, "Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    // ...
                } else {
                    // Sign in failed, display a message and update the UI

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(applicationContext, "Invalid Code", Toast.LENGTH_SHORT)
                            .show()
                        otp.setText("")
                    }
                }
            }
    }


}
