package com.example.sellbuy

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.AnnuncioViewModel
import models.FirebaseDbWrapper
import models.RicercaSalvata


class RicercaActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var searchView: SearchView
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    var filteredList: MutableList<AnnuncioViewModel> = mutableListOf()
    var parola:String = ""
    private var parolaDigitata: String = ""
    private var prezzoDigitato:String = ""
    private var spedizioneDigitata: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ricerca)
        title="Ricerca"
        val recyclerview = findViewById<RecyclerView>(R.id.listView_search)
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, filteredList)
        recyclerview.adapter = adapter

        //qui si prendono i parametri che arrivano da ricerca salvata
        parolaDigitata = intent.getStringExtra("parolaDigitata").toString()
        prezzoDigitato = intent.getStringExtra("prezzo").toString()
        spedizioneDigitata = intent.getStringExtra("spedizione").toString()


        val emailLoggato = FirebaseAuth.getInstance().currentUser?.email
        //gestione filtri
        val opzSpedizione = resources.getStringArray(R.array.spedizioni)
        val adapterSped = ArrayAdapter(this, R.layout.list_item, opzSpedizione)
        val spedizioni = findViewById<AutoCompleteTextView>(R.id.spedizioneAcTv)
        spedizioni.setText(spedizioneDigitata) //imposto il valore che arriva dall'esterno
        spedizioni.setAdapter(adapterSped)
        val prezzo = findViewById<TextInputLayout>(R.id.prezzo_max)
        prezzo.editText?.setText(prezzoDigitato) //imposto il valore che arriva dall'esterno
        val btnCerca= findViewById<Button>(R.id.btn_filtri)
        val btnSalvaRicerca = findViewById<TextView>(R.id.salvaRicerca)
        searchView=findViewById(R.id.search_view)
        searchView.setQuery(parolaDigitata, false) //imposto il valore che arriva dall'esterno

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //when user confirm the search
            override fun onQueryTextSubmit(p0: String?): Boolean {
                parola = p0 ?: ""
                parolaDigitata = parola
                return false
            }
            // when user is writing
            override fun onQueryTextChange(p0: String?): Boolean {
                parola = p0 ?: ""
                parolaDigitata=parola
                return false
            }
        })

        btnCerca.setOnClickListener {
            prezzoDigitato=prezzo.editText?.text.toString()
            spedizioneDigitata= spedizioni.text.toString()
            val b = checkFilters(prezzoDigitato)
            if (b) {
                filteredList = createList(
                    parolaDigitata,
                    prezzoDigitato,
                    spedizioneDigitata
                )
            } else {
                filteredList = createList(parolaDigitata, "10000", spedizioneDigitata)
            }
        }


        btnSalvaRicerca.setOnClickListener {
            val ricerca = RicercaSalvata(
                emailLoggato!!,
                parola,
                prezzo.editText?.text.toString(),
                spedizioni.text.toString(),
                ""
            )
            FirebaseDbWrapper(applicationContext).creaRicercaSalvata(ricerca)
            Toast.makeText(
                applicationContext,
                "Ricerca salvata",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    // controlla se viene inserito il prezzo
    fun checkFilters(prezzo : String) : Boolean{
        return prezzo != ""
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
                        imageName?.let { AnnuncioViewModel(it, nomeAn, prezzoAn, codice) }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ricerca, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val firebaseAuth = FirebaseAuth.getInstance()
        when (item.itemId) {
            R.id.ricercheSalvate -> {
                val intent = Intent(applicationContext, RicercheSalvateActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}