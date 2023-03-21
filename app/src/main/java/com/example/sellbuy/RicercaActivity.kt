package com.example.sellbuy

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import models.Annuncio
import models.AnnuncioViewModel
import models.FirebaseDbWrapper


class RicercaActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var searchView: SearchView
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    var filteredList: MutableList<AnnuncioViewModel> = mutableListOf()
    var parola:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ricerca)
        title="Ricerca"
        val recyclerview = findViewById<RecyclerView>(R.id.listView_search)
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, filteredList)
        recyclerview.adapter = adapter

        //gestione filtri
        val opzSpedizione = resources.getStringArray(R.array.spedizioni)
        val adapterSped = ArrayAdapter(this, R.layout.list_item, opzSpedizione)
        val spedizioni = findViewById<AutoCompleteTextView>(R.id.spedizioneAcTv)
        spedizioni.setText("Tutti")
        spedizioni.setAdapter(adapterSped)
        val prezzo = findViewById<TextInputLayout>(R.id.prezzo_max)

        val btnFiltri= findViewById<Button>(R.id.btn_filtri)
        searchView=findViewById(R.id.search_view)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //when user confirm the search
            override fun onQueryTextSubmit(p0: String?): Boolean {
                /*if(p0.isNullOrEmpty()) {
                    parola=""
                }
                else {

                }*/
                //parola = p0
                parola = p0 ?: ""
                val b=checkFilters(prezzo.editText?.text.toString())
                if(b) {
                    filteredList = createList(parola!!,prezzo.editText?.text.toString(), spedizioni.text.toString())
                }
                else {
                    filteredList = createList(parola!!,"10000", spedizioni.text.toString())

                }
                return false
            }

            // when user is writing
            override fun onQueryTextChange(p0: String?): Boolean {
                parola = p0 ?: ""
                val b=checkFilters(prezzo.editText?.text.toString())
                if(b) {
                    filteredList = createList(parola!!,prezzo.editText?.text.toString(), spedizioni.text.toString())
                }
                else {
                    filteredList = createList(parola!!,"10000", spedizioni.text.toString())

                }
                return false
            }
        })



        btnFiltri.setOnClickListener {
            val b=checkFilters(prezzo.editText?.text.toString())
            if(b) {
                filteredList = createList(parola!!, prezzo.editText?.text.toString(), spedizioni.text.toString())
            }
            else {
                filteredList = createList(parola!!,"10000", spedizioni.text.toString())
            }
        }
    }




    fun checkFilters(prezzo : String) : Boolean{
        if(prezzo== "") {
            return false
        }
        else {
            return true
        }
    }


    fun createList(parola: String, prezzo: String, spedizione : String): MutableList<AnnuncioViewModel> {
        var count = 0
        if (auth.currentUser != null) {
            GlobalScope.launch {
                val an = FirebaseDbWrapper(applicationContext).ricercaConFiltri(applicationContext, parola, prezzo, spedizione)
                val codici = FirebaseDbWrapper(applicationContext).ricercaKeysFromFiltri(applicationContext, parola, prezzo, spedizione)
                filteredList.clear()
                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0) //get the filename from the edit text
                    val prezzoAn = record.prezzo
                    val codice = codici[count]
                    val nuovoan =
                        imageName?.let { AnnuncioViewModel(it, nomeAn, prezzoAn, codice!!) }
                    if (nuovoan != null) {
                        filteredList.add(nuovoan)
                    }
                    count += 1
                }
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
        return filteredList
    }
}