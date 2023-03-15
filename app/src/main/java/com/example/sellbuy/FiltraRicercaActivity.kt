package com.example.sellbuy

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.Annuncio
import models.FirebaseDbWrapper

class FiltraRicercaActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtri_ricerca)

        val parola= intent.getStringExtra("parola")

        val opzSpedizione = resources.getStringArray(R.array.spedizioni)
        val adapterSped = ArrayAdapter(this, R.layout.list_item, opzSpedizione)
        val spedizioni = findViewById<AutoCompleteTextView>(R.id.spedizioneAcTv)
        spedizioni.setAdapter(adapterSped)
        val prezzoMax = findViewById<TextInputLayout>(R.id.prezzo_max)

    }


}