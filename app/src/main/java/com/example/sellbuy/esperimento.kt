package com.example.sellbuy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class esperimento: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.esperimento)

        val btn= findViewById<Button>(R.id.btn_esp)

        btn.setOnClickListener {
            val intent = Intent( applicationContext, AreaPersonaleActivity::class.java)
            startActivity(intent)
        }
    }

}