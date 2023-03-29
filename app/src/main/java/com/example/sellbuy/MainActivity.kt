package com.example.sellbuy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.media.audiofx.BassBoost
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import models.AnnuncioViewModel
import models.FirebaseDbWrapper


class MainActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    //private var storage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://sellbuy-abe26.appspot.com")
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    var mList: MutableList<AnnuncioViewModel> = mutableListOf()

    //gestione notifiche
    private lateinit var notificationManager: NotificationManager
    private val CHANNEL_ID = "it.uniupo.news"
    private val NOTIFICATION_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        /*
        //gestione notifiche push
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Ricevi il token del dispositivo
            val token = task.result
            Log.d(TAG, "FCM token: $token")
            // Qui puoi inviare il token del dispositivo al tuo server per inviare notifiche push
            // al dispositivo
        })
*/

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        title=""
        //mList= createList()
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, mList)
        recyclerview.adapter = adapter

        val loc = findViewById<Button>(R.id.loc)
        loc.setOnClickListener {
            val intent = Intent(applicationContext, esperimento::class.java)
            startActivity(intent)
        }

    }


    override fun onStart() {
        super.onStart()
        mList = createList()
        GlobalScope.launch {
            if(controllaAnnunciFromRicerca()) {
                sendNotification()
            }
        }
    }


    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, "UPO News", importance)
        channel.description = "All the news from your university"
        notificationManager.createNotificationChannel(channel)

        channel.lightColor = Color.RED
        //prova
        /*
        channel.setVibrationPattern(longArrayOf(0, 1000, 500, 1000))
        channel.enableVibration(true)
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null)
        channel.setShowBadge(true)
        channel.setBypassDnd(true)*/



    }

    private fun sendNotification() {
        val resultIntent = Intent(this, MainActivity::class.java)
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


    // Return true if there are new Annunci in a RicercaSalvata
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
                val annunciVecchi = ric.elencoAnnunciTrovati
                //Facciamo un'altra ricerca da confrontare con quella nel DB
                val annunciTrovati = FirebaseDbWrapper(applicationContext).ricercaConFiltri(applicationContext,parola,prezzo,spedizione)
                for(an in annunciTrovati) {
                    var prova=false
                    for(aVecchio in annunciVecchi) {
                        //true if there are no changes
                        if(aVecchio.id==an.id) {
                            prova=true
                        }
                    }
                    if(!prova) {
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



    fun createList(): MutableList<AnnuncioViewModel> {
        //var mList:MutableList<AnnuncioViewModel> = mutableListOf()
        var count = 0
        if (auth.currentUser != null) {
            GlobalScope.launch {
                var an =
                    FirebaseDbWrapper(applicationContext).getTuttiAnnunci(applicationContext)
                var codici =
                    FirebaseDbWrapper(applicationContext).getTutteKeysAnnunci(applicationContext)
                mList.clear()
                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0) //get the filename from the edit text
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
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
            R.id.search -> {
                val parolaDigitata=""
                val prezzo=""
                val spedizione="Tutti"
                val intent = Intent(applicationContext, RicercaActivity::class.java)
                intent.putExtra("parolaDigitata", parolaDigitata)
                intent.putExtra("prezzo", prezzo)
                intent.putExtra("spedizione", spedizione)
                startActivity(intent)
            }
        }
        return true
    }
}
