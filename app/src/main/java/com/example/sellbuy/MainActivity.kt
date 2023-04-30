package com.example.sellbuy

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.*
import models.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    var mList: MutableList<AnnuncioViewModel> = mutableListOf()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =123
    private var currentLatLng : LatLng? = null

    //gestione notifiche
    private lateinit var notificationManager: NotificationManager
    private val CHANNEL_ID = "it.uniupo.news"
    private val NOTIFICATION_ID = 1


    var ris = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        title=""
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, mList)
        recyclerview.adapter = adapter
        // permessi localiz
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


    @OptIn(DelicateCoroutinesApi::class)
    override fun onStart() {
        super.onStart()
        mList = createList()
        GlobalScope.launch {
            if(controllaAnnunciFromRicerca()) {
                sendNotification()
            }
        }
    }


    override fun onBackPressed() {
        finishAffinity()
        finish()
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, "Sell&Buy channel", importance)
        channel.description = "All the news from your app"
        notificationManager.createNotificationChannel(channel)
    }


    private fun sendNotification() {
        val resultIntent = Intent(this, RicercheSalvateActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Annuncio inserito")
            .setContentText("È stato inserito un annuncio relativo alla tua ricerca.")

            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    // Restituisce true se c'è un nuovo annuncio inerente a RicercaSalvata
    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun controllaAnnunciFromRicerca() : Boolean {
        val deferred = GlobalScope.async {
            var ris = false
            val ricerche = FirebaseDbWrapper(applicationContext).getRicercheSalvateFromEmail(applicationContext)
            val chiaviRicerche = FirebaseDbWrapper(applicationContext).getKeysRicercheSalvateFromEmail(applicationContext)
            var count=0
            for(ric in ricerche) {
                val parola = ric.parolaDigitata
                val prezzo = ric.prezzo
                val spedizione = ric.spedizione
                val distanza = ric.localizzazione
                val annunciVecchi = ric.elencoAnnunciTrovati
                //Facciamo un'altra ricerca da confrontare con quella nel DB
                val annunciTrovati = FirebaseDbWrapper(applicationContext).ricercaConFiltriELocalizzazione(applicationContext,parola,prezzo,spedizione,distanza!!,currentLatLng!!)
                for(an in annunciTrovati) {
                    var same=false
                    for(aVecchio in annunciVecchi) {
                        //true se trova l'annuncio
                        if(aVecchio.id==an.id) {
                            same=true
                        }
                        //per gestire che se l'annuncio inserito e inerente alla ricerca è dell'utente loggato non appaia la notifica
                        if(an.email == FirebaseAuth.getInstance().currentUser?.email){
                            same= true
                        }
                    }
                    if(!same) {
                        ris = true
                    }
                }
                // lista aggiornata in ogni caso
                FirebaseDbWrapper(applicationContext).modificaRicercaSalvata(applicationContext,chiaviRicerche[count],annunciTrovati)
                count +=1
            }
            ris
        }
        return deferred.await()
    }




    @SuppressLint("NotifyDataSetChanged")
    fun createList(): MutableList<AnnuncioViewModel> {
        var count = 0
        if (auth.currentUser != null) {
            GlobalScope.launch {
                val an =
                    FirebaseDbWrapper(applicationContext).getTuttiAnnunci(applicationContext)
                val codici =
                    FirebaseDbWrapper(applicationContext).getTutteKeysAnnunci(applicationContext)
                mList.clear()
                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0)
                    val prezzoAn = record.prezzo
                    val codice = codici[count]
                    val nuovoan =
                        imageName?.let { AnnuncioViewModel(it, nomeAn, prezzoAn, codice!!) }
                    if (nuovoan != null) {
                        mList.add(nuovoan)
                    }
                    count += 1
                }
                withContext(Dispatchers.Main) {
                    adapter.notifyDataSetChanged()
                }
            }
        }
        return mList
    }

    private fun eliminaCategoriaDialog(opzioni : Array<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Elimina una categoria")
        var selectedRating = 0
        builder.setSingleChoiceItems(opzioni, selectedRating) { dialog, which ->
            selectedRating = which
        }

        builder.setPositiveButton("Elimina") { dialog, which ->
            val catSel= opzioni[selectedRating]
            GlobalScope.launch {
                FirebaseDbWrapper(applicationContext).deleteCategoria(
                    applicationContext,
                    catSel
                )
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                runOnUiThread {
                    DynamicToast.makeSuccess(applicationContext, "Categoria eliminata", Toast.LENGTH_LONG).show()
                }
                finish()
            }

        }
        builder.setNegativeButton("Annulla") { dialog, which ->
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.show()
    }


    private fun aggiungiCatDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Aggiungi una categoria")

        val input = EditText(this)
        input.hint="Scrivi qui"
        builder.setView(input)
        builder.setPositiveButton("Aggiungi") { dialog, which ->
            val nome = input.text.toString()
            GlobalScope.launch {
                val cat = Categoria(nome.trim().capitalize())
                FirebaseDbWrapper(applicationContext).creaCategoria(cat)
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                runOnUiThread {
                    DynamicToast.makeSuccess(applicationContext, "Categoria aggiunta", Toast.LENGTH_LONG).show()
                }
                finish()

            }

        }
        builder.setNegativeButton("Esci") { dialog, which ->
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.show()
    }


    private fun eliminaUtenteDialog(utenti : Array<String>) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Elimina un utente")
        var selectedRating = 0
        builder.setSingleChoiceItems(utenti, selectedRating) { dialog, which ->
            selectedRating = which
        }

        builder.setPositiveButton("Elimina") { dialog, which ->
            val utenteSelezionato= utenti[selectedRating]
            GlobalScope.launch {
                FirebaseDbWrapper(applicationContext).deleteUtente(applicationContext, utenteSelezionato)
                val utenteEliminato = UtenteEliminato(utenteSelezionato)
                FirebaseDbWrapper(applicationContext).creaUtenteEliminato(utenteEliminato)
                val listaImmaginiDaEliminare =FirebaseDbWrapper(applicationContext).deleteAnnunciUtente(
                    applicationContext,
                    utenteSelezionato
                )
                FirebaseStorageWrapper(applicationContext).deleteImgsFromStorage(applicationContext,listaImmaginiDaEliminare)
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                runOnUiThread {
                    DynamicToast.makeSuccess(applicationContext, "Utente e relativi annunci eliminati", Toast.LENGTH_LONG).show()
                }
                finish()
            }
        }
        builder.setNegativeButton("Annulla") { dialog, which ->
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        CoroutineScope(Dispatchers.IO).launch {
            val bool = FirebaseDbWrapper(applicationContext).isAmministratore(applicationContext)
            withContext(Dispatchers.Main) {
                if(!bool) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }
                else {
                    menuInflater.inflate(R.menu.menu_amministratore, menu)
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val firebaseAuth = FirebaseAuth.getInstance()
        when (item.itemId) {
            R.id.logout -> {
                firebaseAuth.signOut()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.plus -> {
                val intent = Intent(applicationContext, AddActivity::class.java)
                startActivity(intent)
            }
            R.id.chat -> {
                val intent = Intent(applicationContext, ElencoChatActivity::class.java)
                startActivity(intent)
            }
            R.id.profilo -> {
                val intent = Intent(applicationContext, AreaPersonaleActivity::class.java)
                startActivity(intent)
            }
            R.id.profiloAmm -> {
                val intent = Intent(applicationContext, AreaPersonaleAmministratoreAct::class.java)
                startActivity(intent)
            }
            R.id.search -> {
                val parolaDigitata=""
                val prezzo=""
                val spedizione="Tutti"
                val distanza = ""
                val intent = Intent(applicationContext, RicercaActivity::class.java)
                intent.putExtra("parolaDigitata", parolaDigitata)
                intent.putExtra("prezzo", prezzo)
                intent.putExtra("spedizione", spedizione)
                intent.putExtra("distanza", distanza)
                startActivity(intent)
            }
            R.id.aggiungi_cat -> {
                aggiungiCatDialog()
            }
            R.id.elimina_cat -> {
                CoroutineScope(Dispatchers.IO).launch() {
                    val options= FirebaseDbWrapper(applicationContext).getCategorie(applicationContext).toTypedArray()
                    withContext(Dispatchers.Main) {
                        eliminaCategoriaDialog(options)
                    }
                }
            }
            R.id.elimina_utenti -> {
                CoroutineScope(Dispatchers.IO).launch() {
                    val options= FirebaseDbWrapper(applicationContext).getUtenti(applicationContext).toTypedArray()
                    withContext(Dispatchers.Main) {
                        eliminaUtenteDialog(options)
                    }
                }
            }
        }
        return true
    }
}