package com.example.sellbuy

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AcquistoAvvenuto: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acquisto_avvenuto)
        title=""
        val soldiRimasti = intent.getStringExtra("soldiRimasti")
        val txtvCredito = findViewById<TextView>(R.id.soldi_rimasti)
        Log.i(TAG, "Soldi: $soldiRimasti")
        txtvCredito.text = soldiRimasti
        val btnHome = findViewById<Button>(R.id.back_to_home)

        btnHome.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }

    }
}