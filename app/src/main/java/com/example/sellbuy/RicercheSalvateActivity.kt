package com.example.sellbuy

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.AnnuncioViewModel
import models.FirebaseDbWrapper
import models.RicercaSalvata

class RicercheSalvateActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var adapter = RicercaSalvataAdapter(this, mutableListOf())
    var listaRic: MutableList<RicercaSalvata> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ricerche_salvate)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview_ricerche)
        title = "Ricerche Salvate"
        //mList= createList()
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RicercaSalvataAdapter(applicationContext, listaRic)
        recyclerview.adapter = adapter

    }

    override fun onStart() {
        super.onStart()
        listaRic = createList()
    }


    private fun createList(): MutableList<RicercaSalvata> {
        if (auth.currentUser != null) {
            GlobalScope.launch {
                var ricerche =
                    FirebaseDbWrapper(applicationContext).getRicercheSalvateFromEmail(applicationContext)
                listaRic.clear()
                for (record in ricerche) {
                    val email= record.email
                    val parola = record.parolaDigitata
                    val prezzo = record.prezzo
                    val spedizione = record.spedizione
                    val localizzazione = record.localizzazione
                    val nuovaRicerca = RicercaSalvata(email,parola,prezzo,spedizione,localizzazione!!)
                    if (nuovaRicerca != null) {
                        listaRic.add(nuovaRicerca)
                    }
                }
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
                Log.i(TAG,"ricercheeeee $ricerche")
            }
        }
        Log.i(TAG,"ricercheeeee fuori  $listaRic")
        return listaRic
    }
}
