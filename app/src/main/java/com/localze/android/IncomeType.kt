package com.localze.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_income_type.*

class IncomeType : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_type)

        cardCOD.setOnClickListener {
            val intent = Intent(this, IncomeStatus::class.java)
            intent.putExtra("IncomeType", "COD")
            startActivity(intent)
            finish()
        }
        cardPAYTM.setOnClickListener {
            val intent = Intent(this, IncomeStatus::class.java)
            intent.putExtra("IncomeType", "PAYTM")
            startActivity(intent)
            finish()
        }
        cardRAZORPAY.setOnClickListener {
            val intent = Intent(this, IncomeStatus::class.java)
            intent.putExtra("IncomeType", "RAZORPAY")
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Home_seller::class.java)
        startActivity(intent)
        finish()
    }
}