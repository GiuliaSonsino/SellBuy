package com.example.sellbuy

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import models.AnnuncioViewModel
import models.FirebaseDbWrapper

class AreaPersonaleActivity: AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private var adapter = AnnuncioAdapter(this, mutableListOf())
    private var mList: MutableList<AnnuncioViewModel> = mutableListOf()
    private var job: Job? = null
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.area_personale)
        val emailUtente: String? = auth.currentUser?.email
        val nomeUtente = findViewById<TextView>(R.id.nomeUtente)
        nomeUtente.text = emailUtente
        val ruolo = findViewById<TextView>(R.id.ruolo)
        val tel = findViewById<TextView>(R.id.num_tel)
        GlobalScope.launch {
            val utente = FirebaseDbWrapper(applicationContext).getUtenteFromEmail(applicationContext)
            val ruoloo = utente!!.isAmministratore
            val numTell =utente!!.numTel
            if(ruoloo) {
                ruolo.text= "Amministratore"
                tel.text=numTell.toString()
            }
            else {
                ruolo.text = "Utente"
                tel.text=numTell.toString()
            }
        }
        tel.setOnClickListener {
            val phoneUri = Uri.parse("tel:${tel.text}")
            val phoneIntent = Intent(Intent.ACTION_DIAL, phoneUri)
            startActivity(phoneIntent)
        }
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerviewPersonale)
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, mList)
        recyclerview.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        job = GlobalScope.launch(Dispatchers.Main) {
            mList.clear()
            createList()?.let {
                mList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }



    private fun createList(): MutableList<AnnuncioViewModel> {
        count=0
        if (auth.currentUser != null) {
            GlobalScope.launch {
                val codici = FirebaseDbWrapper(applicationContext).getKeyFromEmail(applicationContext)
                Log.i(TAG,"codici utente trivati $codici")
                val an = FirebaseDbWrapper(applicationContext).getAnnunciFromEmail(applicationContext)
                Log.i(TAG,"annunci utente trivati $an")
                mList.clear()
                for (record in an) {
                    val nomeAn = record.nome
                    val imageName = record.foto?.get(0)
                    val prezzoAn = record.prezzo
                    val codice = codici[count]
                    //val codice = if (count < codici.size) codici[count] else "012334"
                    Log.i(TAG,"codice associato $codice")
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
        menuInflater.inflate(R.menu.menu_to_home, menu)
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
            R.id.home -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}