package com.example.sellbuy

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.AnnuncioViewModel
import models.FirebaseDbWrapper
import models.RicercaSalvata
import com.google.android.gms.maps.model.LatLng
import android.Manifest
import android.annotation.SuppressLint


class RicercaActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var searchView: SearchView
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    var filteredList: MutableList<AnnuncioViewModel> = mutableListOf()
    var parola:String = ""
    private var parolaDigitata: String = ""
    private var prezzoDigitato:String = ""
    private var spedizioneDigitata: String = ""
    private var distanzaDigitata: String = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =123
    private var currentLatLng : LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ricerca)
        title = "Ricerca"
        val recyclerview = findViewById<RecyclerView>(R.id.listView_search)
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, filteredList)
        recyclerview.adapter = adapter

        //qui si prendono i parametri che arrivano da ricerca salvata
        parolaDigitata = intent.getStringExtra("parolaDigitata").toString()
        prezzoDigitato = intent.getStringExtra("prezzo").toString()
        spedizioneDigitata = intent.getStringExtra("spedizione").toString()
        distanzaDigitata = intent.getStringExtra("distanza").toString()

        val emailLoggato = FirebaseAuth.getInstance().currentUser?.email
        //gestione filtri
        val opzSpedizione = resources.getStringArray(R.array.spedizioni)
        val adapterSped = ArrayAdapter(this, R.layout.list_item, opzSpedizione)
        val spedizioni = findViewById<AutoCompleteTextView>(R.id.spedizioneAcTv)
        spedizioni.setText(spedizioneDigitata) //imposto il valore che arriva dall'esterno
        spedizioni.setAdapter(adapterSped)
        val prezzo = findViewById<TextInputLayout>(R.id.prezzo_max)
        prezzo.editText?.setText(prezzoDigitato) //imposto il valore che arriva dall'esterno
        val distanza = findViewById<TextInputLayout>(R.id.distanza)
        distanza.editText?.setText(distanzaDigitata) //imposto il valore che arriva dall'esterno
        val btnCerca = findViewById<Button>(R.id.btn_filtri)
        val btnSalvaRicerca = findViewById<TextView>(R.id.salvaRicerca)
        searchView = findViewById(R.id.search_view)
        searchView.setQuery(parolaDigitata, false) //imposto il valore che arriva dall'esterno

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                parola = p0 ?: ""
                parolaDigitata = parola
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                parola = p0 ?: ""
                parolaDigitata = parola
                return false
            }
        })

        btnCerca.setOnClickListener {
            prezzoDigitato = prezzo.editText?.text.toString()
            spedizioneDigitata = spedizioni.text.toString()
            distanzaDigitata = distanza.editText?.text.toString()
            val b = checkFilters(prezzoDigitato)
            if (b) {
                filteredList = createList(
                    parolaDigitata,
                    prezzoDigitato,
                    spedizioneDigitata,
                    distanzaDigitata
                )
            } else {
                filteredList = createList(parolaDigitata, "10000", spedizioneDigitata, distanzaDigitata)
            }
        }


        btnSalvaRicerca.setOnClickListener {
            GlobalScope.launch {
                val b = checkFilters(prezzo.editText?.text.toString())
                if (b) {
                    val annunciFromRicerca = FirebaseDbWrapper(applicationContext).ricercaConFiltriELocalizzazione(
                        applicationContext,
                        parola,
                        prezzo.editText?.text.toString(),
                        spedizioni.text.toString(),
                        distanza.editText?.text.toString(),
                        currentLatLng!!
                    )
                    val ricerca = RicercaSalvata(
                        emailLoggato!!,
                        parola,
                        prezzo.editText?.text.toString(),
                        spedizioni.text.toString(),
                        distanza.editText?.text.toString(),
                        annunciFromRicerca
                    )
                    FirebaseDbWrapper(applicationContext).creaRicercaSalvata(ricerca)
                    runOnUiThread() {
                        DynamicToast.makeSuccess(
                            applicationContext,
                            "Ricerca salvata",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else {
                    val annunciFromRicerca = FirebaseDbWrapper(applicationContext).ricercaConFiltriELocalizzazione(
                        applicationContext,
                        parola,
                        "10000",
                        spedizioni.text.toString(),
                        distanza.editText?.text.toString(),
                        currentLatLng!!
                    )
                    val ricerca = RicercaSalvata(
                        emailLoggato!!,
                        parola,
                        "10000",
                        spedizioni.text.toString(),
                        distanza.editText?.text.toString(),
                        annunciFromRicerca
                    )
                    FirebaseDbWrapper(applicationContext).creaRicercaSalvata(ricerca)
                    runOnUiThread() {
                        DynamicToast.makeSuccess(
                            applicationContext,
                            "Ricerca salvata",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // permission per localiz
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

        }
        else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if(location!=null) {
                    currentLatLng = LatLng(location.latitude,location.longitude)
                }
            }
        }
    }


    // controlla se viene inserito il prezzo
    fun checkFilters(prezzo : String) : Boolean{
        return prezzo != ""
    }


    @SuppressLint("NotifyDataSetChanged")
    fun createList(parola: String, prezzo: String, spedizione : String, distanza : String): MutableList<AnnuncioViewModel> {
        var count = 0
        if (auth.currentUser != null) {
            GlobalScope.launch {
                val an = FirebaseDbWrapper(applicationContext).ricercaConFiltriELocalizzazione(applicationContext, parola, prezzo, spedizione,distanza,currentLatLng!!)
                val codici = FirebaseDbWrapper(applicationContext).ricercaKeysConFiltriELocalizzazione(applicationContext, parola, prezzo, spedizione,distanza, currentLatLng!!)
                filteredList.clear()
                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0)
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
        when (item.itemId) {
            R.id.ricercheSalvate -> {
                val intent = Intent(applicationContext, RicercheSalvateActivity::class.java)
                startActivity(intent)
            }
            R.id.home -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}