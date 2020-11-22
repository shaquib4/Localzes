package com.example.localzes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast

class continue_payment : AppCompatActivity() {

    lateinit var radioGroup : RadioGroup
    lateinit var button : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_continue_payment)

        radioGroup = findViewById(R.id.radioPayment)

        findViewById<Button>(R.id.btnContinue).setOnClickListener {

            val id = radioGroup.checkedRadioButtonId
            val radioButton = findViewById<RadioButton>(id)

            Toast.makeText(this@continue_payment, "You selected " + radioButton.text, Toast.LENGTH_LONG ).show()
        }

    }
}