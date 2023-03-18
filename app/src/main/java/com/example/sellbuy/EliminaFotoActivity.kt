package com.example.sellbuy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import models.FirebaseDbWrapper
import models.FirebaseStorageWrapper

class EliminaFotoActivity: AppCompatActivity()  {

    private var immagini: MutableList<String> = mutableListOf()
    private var immaginiMod: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_elimina_foto)
        title="Elimina immagine"
        val codiceAnn = intent.getStringExtra("codiceAnn")

        val img1 = findViewById<ImageView>(R.id.img1)
        val img2 = findViewById<ImageView>(R.id.img2)
        val img3 = findViewById<ImageView>(R.id.img3)
        val img4 = findViewById<ImageView>(R.id.img4)
        val img5 = findViewById<ImageView>(R.id.img5)

        val btn1 = findViewById<Button>(R.id.btn_elimina1)
        val btn2 = findViewById<Button>(R.id.btn_elimina2)
        val btn3 = findViewById<Button>(R.id.btn_elimina3)
        val btn4 = findViewById<Button>(R.id.btn_elimina4)
        val btn5 = findViewById<Button>(R.id.btn_elimina5)
        val titolo = findViewById<TextView>(R.id.title)
       // var immagini: MutableList<String> = mutableListOf()
       // var immaginiMod: MutableList<String> = mutableListOf()
        immagini.clear()
        immaginiMod.clear()
        CoroutineScope(Dispatchers.IO).launch {
            val currentAnnuncio = FirebaseDbWrapper(applicationContext).getAnnuncioFromCodice(
                applicationContext,
                codiceAnn!!
            )
            withContext(Dispatchers.Main) { // quando la funz getAnn.. ha recuperato l'annuncio allora fa questo codice seguente
                titolo.setText(currentAnnuncio.nome)
                immagini = currentAnnuncio.foto!!
                //numeroImm = immagini!!.size
            }

                val strMainImm = immagini[0]
                val storage = Firebase.storage.reference.child("images/$strMainImm")
                storage.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                            .override(500, 500).into(img1)
                    }
                }
                if (immagini.size >= 2) {
                    val strImmagine1 = immagini[1]
                    val storage = Firebase.storage.reference.child("images/$strImmagine1")
                    storage.downloadUrl.addOnSuccessListener { url ->
                        if (applicationContext != null) {
                            Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                                .override(500, 500).into(img2!!)
                        }
                    }
                }
                else {
                    img2.visibility = View.INVISIBLE
                    btn2.visibility = View.INVISIBLE
                    // View.INVISIBLE.also { im1!!.visibility = it }
                }

                if (immagini.size >= 3) {
                    val strImmagine2 = immagini[2]
                    val storage = Firebase.storage.reference.child("images/$strImmagine2")
                    storage.downloadUrl.addOnSuccessListener { url ->
                        if (applicationContext != null) {
                            Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                                .override(500, 500).into(img3)
                        }
                    }
                }
                else {
                    img3.visibility = View.INVISIBLE
                    btn3.visibility = View.INVISIBLE
                }

                if (immagini.size >= 4) {
                    val strImmagine3 = immagini[3]
                    val storage = Firebase.storage.reference.child("images/$strImmagine3")
                    storage.downloadUrl.addOnSuccessListener { url ->
                        if (applicationContext != null) {
                            Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                                .override(500, 500).into(img4)
                        }
                    }
                }
                else {
                    img4.visibility = View.INVISIBLE
                    btn4.visibility = View.INVISIBLE
                }

                if (immagini.size >= 5) {
                    val strImmagine4 = immagini[4]
                    val storage = Firebase.storage.reference.child("images/$strImmagine4")
                    storage.downloadUrl.addOnSuccessListener { url ->
                        if (applicationContext != null) {
                            Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                                .override(500, 500).into(img5)
                        }
                    }
                }
                else {
                    img5.visibility = View.INVISIBLE
                    btn5.visibility = View.INVISIBLE
                }


        }

                btn1.setOnClickListener {
                    if(immagini.size==1) {
                        Toast.makeText(
                            applicationContext,
                            "Impossibile eliminare tutte le immagini",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        val nomeFoto = immagini[0]
                        immaginiMod = immagini
                        immaginiMod.remove(nomeFoto)
                        CoroutineScope(Dispatchers.IO).launch {
                            FirebaseDbWrapper(applicationContext).modificaImmagineFromAnnuncio(
                                applicationContext,
                                codiceAnn!!,
                                immaginiMod
                            )
                            FirebaseStorageWrapper(applicationContext).deleteImmagineFromStorage(
                                applicationContext,
                                nomeFoto
                            )
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    applicationContext,
                                    "Immagine eliminata con successo",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent =
                                    Intent(applicationContext, AreaPersonaleActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }

                        }
                    }
                }

                btn2.setOnClickListener {
                    val nomeFoto = immagini[1]
                    immaginiMod = immagini
                    immaginiMod.remove(nomeFoto)
                    CoroutineScope(Dispatchers.IO).launch {
                        FirebaseDbWrapper(applicationContext).modificaImmagineFromAnnuncio(
                            applicationContext,
                            codiceAnn!!,
                            immaginiMod
                        )
                        FirebaseStorageWrapper(applicationContext).deleteImmagineFromStorage(
                            applicationContext,
                            nomeFoto
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "Immagine eliminata con successo",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent =
                                Intent(applicationContext, AreaPersonaleActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                    }

                }

                btn3.setOnClickListener {
                    val nomeFoto = immagini[2]
                    immaginiMod = immagini
                    immaginiMod.remove(nomeFoto)
                    CoroutineScope(Dispatchers.IO).launch {
                        FirebaseDbWrapper(applicationContext).modificaImmagineFromAnnuncio(
                            applicationContext,
                            codiceAnn!!,
                            immaginiMod
                        )
                        FirebaseStorageWrapper(applicationContext).deleteImmagineFromStorage(
                            applicationContext,
                            nomeFoto
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "Immagine eliminata con successo",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(applicationContext,AreaPersonaleActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                    }

                }

                btn4.setOnClickListener {
                    val nomeFoto = immagini[3]
                    immaginiMod = immagini
                    immaginiMod.remove(nomeFoto)
                    CoroutineScope(Dispatchers.IO).launch {
                        FirebaseDbWrapper(applicationContext).modificaImmagineFromAnnuncio(
                            applicationContext,
                            codiceAnn!!,
                            immaginiMod
                        )
                        FirebaseStorageWrapper(applicationContext).deleteImmagineFromStorage(
                            applicationContext,
                            nomeFoto
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "Immagine eliminata con successo",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(applicationContext, AreaPersonaleActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                    }
                }

                btn5.setOnClickListener {
                    val nomeFoto = immagini[4]
                    immaginiMod = immagini
                    immaginiMod.remove(nomeFoto)
                    CoroutineScope(Dispatchers.IO).launch {
                        FirebaseDbWrapper(applicationContext).modificaImmagineFromAnnuncio(
                            applicationContext,
                            codiceAnn!!,
                            immaginiMod
                        )
                        FirebaseStorageWrapper(applicationContext).deleteImmagineFromStorage(
                            applicationContext,
                            nomeFoto
                        )
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                applicationContext,
                                "Immagine eliminata con successo",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(applicationContext, AreaPersonaleActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                    }

                }

      //  }
    }
}