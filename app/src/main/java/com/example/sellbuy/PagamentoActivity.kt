package com.example.sellbuy


import android.content.Intent
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.*
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
        val codiceUtente = intent.getStringExtra("codiceUtente")
        titolare = findViewById(R.id.titolareAcTv)
        numero = findViewById(R.id.numeroAcTv)
        cvv = findViewById(R.id.CVVAcTv)
        importo = findViewById(R.id.importoAcTv)
        val btnImporto = findViewById<Button>(R.id.btnImporto)


        fun checkPagamento(): Boolean {
            if (titolare!!.text.toString() == "" || numero!!.text.toString() == "" || cvv!!.text.toString() == "" || importo!!.text.toString() == "") {
                runOnUiThread {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Compilare tutti i campi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                    return false
                }

            else if (numero!!.text.toString().length < 16) {
                runOnUiThread {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "Numero carta deve essere di 16 cifre",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return false
            }

            else if (cvv!!.text.toString().length < 3) {
                runOnUiThread {
                    DynamicToast.makeWarning(
                        applicationContext,
                        "CVV carta deve essere di 3 cifre",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return false
            }
            else {
                return true
            }
        }

        btnImporto.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val utente =
                    FirebaseDbWrapper(applicationContext).getUtenteFromEmail(applicationContext)
                withContext(Dispatchers.Main) {
                    if (checkPagamento()) {
                        val creditoAggiornato = utente!!.credito + importo!!.text.toString().toDouble()
                        FirebaseDbWrapper(applicationContext).modificaCreditoUtente(
                            applicationContext,
                            codiceUtente!!,
                            creditoAggiornato
                        )
                        runOnUiThread {
                            DynamicToast.makeSuccess(
                                applicationContext,
                                "Ricarica avvenuta",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }
}



