package com.example.sellbuy

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.FirebaseDbWrapper

class AnnuncioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annuncio)


        val im1=findViewById<ImageView>(R.id.image1)
        GlobalScope.launch {
            val codiceAnn = intent.getStringExtra("codice")
            var ann =
                FirebaseDbWrapper(applicationContext).getAnnuncioFromCodice(
                    applicationContext,
                    codiceAnn!!
                )
            val titolo = findViewById<TextView>(R.id.tv_title)
            titolo.text = ann.nome
            val descrizione = findViewById<TextView>(R.id.tv_description)
            descrizione.text = ann.descrizione

            val immagini=ann.foto
            val immagine1= immagini?.get(0)
            val storag= Firebase.storage.reference.child("images/$immagine1")
            storag.downloadUrl.addOnSuccessListener { url ->
                if (applicationContext != null) {
                    Glide.with(applicationContext).load(url).into(im1)
                }
            }

        }
            val dialog = Dialog(this)

            // Imposta le proprietà del dialog, ad esempio le dimensioni
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window!!.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT

            //val im1 = findViewById<ImageView>(R.id.image1)
            im1?.setOnClickListener {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_box)

                // Trova la ImageView nella vista del dialog
                val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
                // Imposta l'immagine ingrandita sulla ImageView del dialog
                dialogImage.setImageResource(R.drawable.person)
                // Mostra il dialog
                dialog.show()
            }

            val im2 = findViewById<ImageView>(R.id.image2)
            im2?.setOnClickListener {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_box)

                // Trova la ImageView nella vista del dialog
                val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
                // Imposta l'immagine ingrandita sulla ImageView del dialog
                dialogImage.setImageResource(R.drawable.person)
                // Mostra il dialog
                dialog.show()
            }

            val im3 = findViewById<ImageView>(R.id.image3)
            im3?.setOnClickListener {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_box)

                // Trova la ImageView nella vista del dialog
                val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
                // Imposta l'immagine ingrandita sulla ImageView del dialog
                dialogImage.setImageResource(R.drawable.person)
                // Mostra il dialog
                dialog.show()
            }

            val im4 = findViewById<ImageView>(R.id.image4)
            im4?.setOnClickListener {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_box)

                // Trova la ImageView nella vista del dialog
                val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
                // Imposta l'immagine ingrandita sulla ImageView del dialog
                dialogImage.setImageResource(R.drawable.plus)
                // Mostra il dialog
                dialog.show()
            }

            val btnChat = findViewById<Button>(R.id.btnChat)
            btnChat.setOnClickListener {
                val intent = Intent(this, ChatActivity::class.java)
                startActivity(intent)
            }

        }

}