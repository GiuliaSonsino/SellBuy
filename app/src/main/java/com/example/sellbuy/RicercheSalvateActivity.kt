package com.example.sellbuy

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.FirebaseDbWrapper
import models.RicercaSalvata

class RicercheSalvateActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var adapter = RicercaSalvataAdapter(this, mutableListOf())
    private var listaRic: MutableList<RicercaSalvata> = mutableListOf()
    private var numRicerche: TextView? = null
    private var listener: ListCompleteListener? = null
    private var num : Int = 0

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =123
    private var currentLatLng : LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ricerche_salvate)

        numRicerche = findViewById(R.id.num_ricerche)
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview_ricerche)
        title = "Ricerche Salvate"
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RicercaSalvataAdapter(applicationContext, listaRic)
        recyclerview.adapter = adapter

        setListCompleteListener(object : ListCompleteListener {
            override fun onListCompleted(size: Int) {
                numRicerche!!.text = size.toString()
            }
        })

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

    override fun onStart() {
        super.onStart()
        listaRic = createList()
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun createList(): MutableList<RicercaSalvata> {
        if (auth.currentUser != null) {
            GlobalScope.launch {
                val ricerche =
                    FirebaseDbWrapper(applicationContext).getRicercheSalvateFromEmail(applicationContext)
                listaRic.clear()
                for (record in ricerche) {
                    val email= record.email
                    val parola = record.parolaDigitata
                    val prezzo = record.prezzo
                    val spedizione = record.spedizione
                    val localizzazione = record.localizzazione
                    val annunci = FirebaseDbWrapper(applicationContext).ricercaConFiltriELocalizzazione(applicationContext,parola,prezzo,spedizione,localizzazione!!,currentLatLng!!)
                    val nuovaRicerca = RicercaSalvata(email,parola,prezzo,spedizione,localizzazione, annunci)
                    if (nuovaRicerca != null) {
                        listaRic.add(nuovaRicerca)
                    }
                }
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                    num = listaRic.size
                    numRicerche!!.text = num.toString()
                    listener?.onListCompleted(num)
                }
            }
        }
        num = listaRic.size
        return listaRic
    }

    fun setListCompleteListener(listener: ListCompleteListener) {
        this.listener = listener
    }
}


interface ListCompleteListener {
    fun onListCompleted(size: Int)
}
