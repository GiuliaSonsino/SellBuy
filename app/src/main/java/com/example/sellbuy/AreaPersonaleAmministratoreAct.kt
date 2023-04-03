package com.example.sellbuy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import models.AnnuncioViewModel
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

        CoroutineScope(Dispatchers.IO).launch {
            val utente = FirebaseDbWrapper(applicationContext).getUtenteFromEmail(applicationContext)
            withContext(Dispatchers.Main) {
                val numTell = utente!!.numTel
                ruolo.text = "Amministratore"
                tel.text = numTell.toString()
            }
        }

        tel.setOnClickListener {
            val phoneUri = Uri.parse("tel:${tel.text}")
            val phoneIntent = Intent(Intent.ACTION_DIAL, phoneUri)
            startActivity(phoneIntent)
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