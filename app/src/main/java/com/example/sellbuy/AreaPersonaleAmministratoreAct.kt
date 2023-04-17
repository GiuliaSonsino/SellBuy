package com.example.sellbuy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import models.FirebaseDbWrapper

class AreaPersonaleAmministratoreAct : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.area_personale_amministratore)
        title = "Area personale"
        val emailUtente: String? = auth.currentUser?.email
        val nomeUtente = findViewById<TextView>(R.id.nomeUtente)
        nomeUtente.text = emailUtente
        val ruolo = findViewById<TextView>(R.id.ruolo)
        val tel = findViewById<TextView>(R.id.num_tel)
        val numUtentiTot = findViewById<TextView>(R.id.num_utenti_tot)
        val numAnnunciTot = findViewById<TextView>(R.id.num_oggetti_tot)
        val utentiEdit = findViewById<AutoCompleteTextView>(R.id.edit_utenti)
        val btnMostraAnnunciUtente = findViewById<Button>(R.id.btn_mostra_annunci)
        val numAnnunciUtente = findViewById<TextView>(R.id.num_annunci_utente)
        numAnnunciUtente.text=""

        CoroutineScope(Dispatchers.IO).launch {
            val listaAnnunci = FirebaseDbWrapper(applicationContext).getTuttiAnnunci(applicationContext)
            val utente = FirebaseDbWrapper(applicationContext).getUtenteFromEmail(applicationContext)
            val listaUtenti = FirebaseDbWrapper(applicationContext).getUtenti(applicationContext)
            withContext(Dispatchers.Main) {
                val numTell = utente!!.numTel
                ruolo.text = "Amministratore"
                tel.text = numTell.toString()

                val num = listaAnnunci.size
                numAnnunciTot.text = num.toString()

                val adapterUtenti = ArrayAdapter(applicationContext, R.layout.list_item, listaUtenti)
                utentiEdit.setAdapter(adapterUtenti)

                val numUtenti = listaUtenti.size
                numUtentiTot.text = numUtenti.toString()
            }
        }

        btnMostraAnnunciUtente.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val num = FirebaseDbWrapper(applicationContext).getNumeroAnnunciFromEmailUtente(applicationContext, utentiEdit.text.toString())
                withContext(Dispatchers.Main) {
                    numAnnunciUtente.text = num.toString()
                }
            }
        }

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