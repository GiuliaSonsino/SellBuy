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

class AcquistoAvvenuto: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acquisto_avvenuto)
        title=""
        val soldiRimasti = intent.getStringExtra("soldiRimasti")
        val venditore = intent.getStringExtra("venditore")
        val txtvCredito = findViewById<TextView>(R.id.soldi_rimasti)
        Log.i(TAG, "Soldi: $soldiRimasti")
        txtvCredito.text = soldiRimasti
        val btnHome = findViewById<Button>(R.id.back_to_home)

        btnHome.setOnClickListener {
            showVoteDialog()
            /*
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)*/
        }

    }
    /*
    private fun showVoteDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Vota il servizio e lascia una recensione")

        // Opzioni per la selezione della votazione
        val ratingOptions = arrayOf("1", "2", "3", "4", "5")
        var selectedRating = 0 // Valore predefinito

        // Aggiungi le opzioni per la selezione della votazione
        builder.setSingleChoiceItems(ratingOptions, selectedRating) { dialog, which ->
            selectedRating = which
        }

        // Aggiungi un campo di testo per la recensione
        val input = EditText(this)
        builder.setView(input)

        // Aggiungi i bottoni "OK" e "Annulla"
        builder.setPositiveButton("OK") { dialog, which ->
            val rating = ratingOptions[selectedRating]
            val review = input.text.toString()
            // Fai qualcosa con la votazione e la recensione
        }
        builder.setNegativeButton("Annulla", null)

        // Mostra l'AlertDialog
        builder.show()
    }*/

    private fun showVoteDialog() {
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

        builder.setPositiveButton("OK") { dialog, which ->
            val rating = ratingOptions[selectedRating]
            val review = input.text.toString()
            // Fai qualcosa con la votazione e la recensione
        }
        builder.setNegativeButton("Non voglio votare") { dialog, which ->
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.show()
    }
}