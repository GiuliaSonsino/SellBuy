package com.example.sellbuy

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import models.FirebaseDbWrapper
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ModificaAnnActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifica_ann)
        title = "Modifica"
        val codiceAnn = intent.getStringExtra("codiceAnn")
        val catAnn = intent.getStringExtra("catAnn")
        val condAnn = intent.getStringExtra("condAnn")

        val titolo = findViewById<EditText>(R.id.edit_title)
        val descrizione = findViewById<EditText>(R.id.edit_description)
        val prezzo = findViewById<EditText>(R.id.edit_price)
        val spedizione = findViewById<Switch>(R.id.edit_switchSpediz)
        var sped: Boolean? = null
        val btnSalva = findViewById<Button>(R.id.btnSalva)

        val categorie = resources.getStringArray(R.array.categorie)
        val adapterCat = ArrayAdapter(this, R.layout.list_item, categorie)
        val categoriaEdit = findViewById<AutoCompleteTextView>(R.id.edit_categoria)
        categoriaEdit.setText(catAnn)
        categoriaEdit.setAdapter(adapterCat)

        val listaCondizioni = resources.getStringArray(R.array.condizioni)
        val adapterCond = ArrayAdapter(this, R.layout.list_item, listaCondizioni)
        val condizioniEdit = findViewById<AutoCompleteTextView>(R.id.edit_condizioni)
        condizioniEdit.setText(condAnn)
        condizioniEdit.setAdapter(adapterCond)

        CoroutineScope(Dispatchers.IO).launch {
            val currentAnnuncio = FirebaseDbWrapper(applicationContext).getAnnuncioFromCodice(
                applicationContext,
                codiceAnn!!
            )
            withContext(Dispatchers.Main) { // quando la funz getAnn.. ha recuperato l'annuncio allora fa questo codice seguente
                titolo.setText(currentAnnuncio.nome)
                descrizione.setText(currentAnnuncio.descrizione)
                prezzo.setText(currentAnnuncio.prezzo)
                sped = currentAnnuncio.spedizione
                spedizione.isChecked = sped!!

            }
        }


        fun sped() : Boolean {
            return spedizione.isChecked
        }

        btnSalva.setOnClickListener {
            GlobalScope.launch {
                val tit = titolo.text.toString()
                val desc = descrizione.text.toString()
                val prez = prezzo.text.toString()
                val cat = categoriaEdit.text.toString()
                val stato = condizioniEdit.text.toString()
                val s = sped()
                FirebaseDbWrapper(applicationContext).modificaInfoAnnuncio(
                    applicationContext,
                    codiceAnn!!,
                    tit,
                    desc,
                    prez,
                    cat,
                    stato,
                    s
                )
            }
            finish()
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
        }
    }
}
