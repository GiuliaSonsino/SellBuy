package com.example.sellbuy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
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
        title = "Area personale"
        val ricaricaCartaTv= findViewById<TextView>(R.id.ricarica_credito)
        val btnRece = findViewById<Button>(R.id.btn_rece)
        val emailUtente: String? = auth.currentUser?.email
        val nomeUtente = findViewById<TextView>(R.id.nomeUtente)
        nomeUtente.text = emailUtente
        val ruolo = findViewById<TextView>(R.id.ruolo)
        val credito = findViewById<TextView>(R.id.credito)
        val tel = findViewById<TextView>(R.id.num_tel)

        CoroutineScope(Dispatchers.IO).launch {
            val utente = FirebaseDbWrapper(applicationContext).getUtenteFromEmail(applicationContext)
            withContext(Dispatchers.Main) {
                val numTell = utente!!.numTel
                ruolo.text = "Utente"
                tel.text = numTell.toString()
                credito.text = utente.credito.toString()
            }
        }
        btnRece.setOnClickListener{
            val intent = Intent(applicationContext, RecensioniActivity::class.java)
            intent.putExtra("emailRecensioni", FirebaseAuth.getInstance().currentUser!!.email)
            startActivity(intent)
        }
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerviewPersonale)
        recyclerview.layoutManager = LinearLayoutManager(this)
        adapter = AnnuncioAdapter(applicationContext, mList)
        recyclerview.adapter = adapter

        ricaricaCartaTv.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val codiceUtente = FirebaseDbWrapper(applicationContext).getIdUtenteFromEmail(
                    applicationContext,
                    emailUtente!!
                )
                withContext(Dispatchers.Main) {
                    val intent = Intent(applicationContext, PagamentoActivity::class.java)
                    intent.putExtra("codiceUtente", codiceUtente)
                    startActivity(intent)
                }
            }
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        job = GlobalScope.launch(Dispatchers.Main) {
            mList.clear()
            createList().let {
                mList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        job?.cancel()
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun createList(): MutableList<AnnuncioViewModel> {
        count=0
        if (auth.currentUser != null) {
            GlobalScope.launch {
                val codici = FirebaseDbWrapper(applicationContext).getKeyFromEmail(applicationContext)
                val an = FirebaseDbWrapper(applicationContext).getAnnunciFromEmail(applicationContext)
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