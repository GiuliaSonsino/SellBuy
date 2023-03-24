package com.example.sellbuy

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.FirebaseDbWrapper

class PagamentoActivity: AppCompatActivity() {

    private var titolare : AutoCompleteTextView? = null
    private var numero : AutoCompleteTextView? = null
    private var cvv : AutoCompleteTextView? = null
    private var importo : AutoCompleteTextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagamento)
        title="Ricarica"
        val creditoAttuale = intent.getStringExtra("creditoAttuale")
        val codiceUtente = intent.getStringExtra("codiceUtente")
        titolare = findViewById(R.id.titolareAcTv)
        numero = findViewById(R.id.numeroAcTv)
        cvv = findViewById(R.id.CVVAcTv)
        importo = findViewById(R.id.importoAcTv)
        val btnImporto = findViewById<Button>(R.id.btnImporto)



        btnImporto.setOnClickListener {
            GlobalScope.launch {
                val creditoAggiornato = 50.0
                    //creditoAttuale!!.toDouble() + importo!!.text.toString().toDouble()
                if (checkPagamento()) {
                    FirebaseDbWrapper(applicationContext).modificaCreditoUtente(
                        applicationContext,
                        codiceUtente!!,
                        creditoAggiornato
                    )
                    Toast.makeText(
                        applicationContext,
                        "Ricarica avvenuta",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }


    }

    fun checkPagamento(): Boolean {
        if(titolare!!.text.toString()== "" || numero!!.text.toString()== "" || cvv!!.text.toString()== "" ||importo!!.text.toString()== "") {
            Toast.makeText(
                applicationContext,
                "Compilare tutti i campi",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        else if (numero!!.text.toString().length < 16) {
            Toast.makeText(
                applicationContext,
                "Numero carta deve essere di 16 cifre",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        else if (cvv!!.text.toString().length < 3) {
            Toast.makeText(
                applicationContext,
                "CVV carta deve essere di 3 cifre",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        else {
            return true
        }
    }


}