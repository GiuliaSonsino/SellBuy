package com.example.sellbuy

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.FirebaseDbWrapper

class AcquistoAvvenuto: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acquisto_avvenuto)
        title=""
        val soldiRimasti = intent.getStringExtra("soldiRimasti")
        val codiceAnn = intent.getStringExtra("codiceAnn")
        val txtvCredito = findViewById<TextView>(R.id.soldi_rimasti)
        Log.i(TAG, "Soldi: $soldiRimasti")
        txtvCredito.text = soldiRimasti
        val btnHome = findViewById<Button>(R.id.back_to_home)

        btnHome.setOnClickListener {
            showVoteDialog(codiceAnn!!)
        }

    }

    private fun showVoteDialog(codiceAnn : String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Vota il servizio e lascia una recensione")
        val ratingOptions = arrayOf("1", "2", "3", "4", "5")
        var selectedRating = 0 // Valore predefinito
        builder.setSingleChoiceItems(ratingOptions, selectedRating) { dialog, which ->
            selectedRating = which
        }

        val input = EditText(this)
        input.hint="Scrivi qui la recensione"
        builder.setView(input)

        builder.setPositiveButton("Salva") { dialog, which ->
            val rating = ratingOptions[selectedRating]
            val review = input.text.toString()
            GlobalScope.launch {
                FirebaseDbWrapper(applicationContext).modificaRecensioneAcquirente(
                    applicationContext,
                    codiceAnn,
                    rating.toInt(),
                    review,
                    true
                )
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        builder.setNegativeButton("Non voglio votare") { dialog, which ->
            GlobalScope.launch {
                FirebaseDbWrapper(applicationContext).modificaRecensioneAcquirente(
                    applicationContext,
                    codiceAnn,
                    0,
                    "",
                    false
                )
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        builder.show()
    }
}