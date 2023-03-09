package com.example.sellbuy

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.Annuncio
import models.FirebaseDbWrapper
import models.FirebaseStorageWrapper

class AnnuncioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annuncio)

        var em=FirebaseAuth.getInstance().currentUser?.email
        var idCurrentUser:String?=null
        var idProprietario:String?=null
        var nomeArticolo:String?=null
        GlobalScope.launch {
            idCurrentUser=FirebaseDbWrapper(applicationContext).getIdUtenteFromEmail(applicationContext,em!!)
        }

        var ann: Annuncio
        var emailProprietarioAnn: String? = "*"
        var immagini: MutableList<String>? = mutableListOf()
        var strMainImm : String?
        var strImmagine1: String?
        var strImmagine2: String?
        var strImmagine3: String?
        var strImmagine4: String?
        var storag: StorageReference
        val im1=findViewById<ImageView>(R.id.image1)
        val im2=findViewById<ImageView>(R.id.image2)
        val im3=findViewById<ImageView>(R.id.image3)
        val im4=findViewById<ImageView>(R.id.image4)
        val mainImmagine= findViewById<ImageView>(R.id.main_image)

        val codiceAnn = intent.getStringExtra("codice")
        val btnChat = findViewById<Button>(R.id.btnChat)
        val btnElimina= findViewById<Button>(R.id.btnElimina)
        val btnModifica= findViewById<Button>(R.id.btnModifica)
        val btnAcquista= findViewById<Button>(R.id.btnAcquista)


        GlobalScope.launch {
            //val codiceAnn = intent.getStringExtra("codice")
            ann =
                FirebaseDbWrapper(applicationContext).getAnnuncioFromCodice(
                    applicationContext,
                    codiceAnn!!
                )
            val titolo = findViewById<TextView>(R.id.tv_title)
            titolo.text = ann.nome
            val autore = findViewById<TextView>(R.id.tv_autore)
            autore.text = ann.email
            val numTel = findViewById<TextView>(R.id.tv_num_tel)
            numTel.text = ann.numTel.toString()
            val descrizione = findViewById<TextView>(R.id.tv_description)
            descrizione.text = ann.descrizione
            val prezzo = findViewById<TextView>(R.id.tv_price)
            prezzo.text = ann.prezzo
            val condizione = findViewById<TextView>(R.id.tv_condition)
            condizione.text = ann.stato
            val categoria = findViewById<TextView>(R.id.tv_category)
            categoria.text = ann.categoria
            val spedizione = findViewById<TextView>(R.id.tv_spedizione)
            if(ann.spedizione) {
                spedizione.text= "è disposto"
            }
            else {
                spedizione.text= "non è disposto"
            }

            emailProprietarioAnn= ann.email
            nomeArticolo=ann.nome
            idProprietario=FirebaseDbWrapper(applicationContext).getIdUtenteFromEmail(applicationContext,emailProprietarioAnn!!)

            immagini = ann.foto
            strMainImm = immagini?.get(0)
            storag = Firebase.storage.reference.child("images/$strMainImm")
            storag.downloadUrl.addOnSuccessListener { url ->
                if (applicationContext != null) {
                    Glide.with(applicationContext).load(url).into(mainImmagine)
                }
            }
            if(immagini?.size!! >=2) {
                strImmagine1 = immagini?.get(1)
                storag = Firebase.storage.reference.child("images/$strImmagine1")
                storag.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).into(im1)
                    }
                }
            } else {
                View.INVISIBLE.also { im1.visibility = it }
            }

            if(immagini?.size!! >=3) {
                strImmagine2 = immagini?.get(2)
                storag = Firebase.storage.reference.child("images/$strImmagine2")
                storag.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).into(im2)
                    }
                }
            } else {
                im2.visibility= View.INVISIBLE
            }

            if(immagini?.size!! >=4) {
                strImmagine3 = immagini?.get(3)
                storag = Firebase.storage.reference.child("images/$strImmagine3")
                storag.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).into(im3)
                    }
                }
            } else {
                im3.visibility= View.INVISIBLE
            }

            if(immagini?.size!! >=5) {
                strImmagine4 = immagini?.get(4)
                storag = Firebase.storage.reference.child("images/$strImmagine4")
                storag.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).into(im4)
                    }
                }
            } else {
                im4.visibility= View.INVISIBLE
            }


            //handle visibility button
            if(em.equals(ann.email)) {
                btnElimina.visibility= View.VISIBLE
                btnModifica.visibility= View.VISIBLE
                btnAcquista.visibility= View.INVISIBLE
                btnChat.visibility= View.INVISIBLE
            } else {
                btnElimina.visibility= View.INVISIBLE
                btnModifica.visibility= View.INVISIBLE
                btnAcquista.visibility= View.VISIBLE
                btnChat.visibility= View.VISIBLE
            }
        }


        val dialog = Dialog(this)
        // Imposta le proprietà del dialog e le dimensioni
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        mainImmagine?.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_box)
            // Trova la ImageView nella vista del dialog
            val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
            strMainImm = immagini?.get(0)
            storag = Firebase.storage.reference.child("images/$strMainImm")
            storag.downloadUrl.addOnSuccessListener { url ->
                if (applicationContext != null) {
                    Glide.with(applicationContext).load(url).into(dialogImage)
                }
            }
            dialog.show()
        }


        //val im1 = findViewById<ImageView>(R.id.image1)
        im1?.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_box)
            // Trova la ImageView nella vista del dialog
            val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
            strImmagine1 = immagini?.get(1)
            storag = Firebase.storage.reference.child("images/$strImmagine1")
            storag.downloadUrl.addOnSuccessListener { url ->
                if (applicationContext != null) {
                    Glide.with(applicationContext).load(url).into(dialogImage)
                }
            }
            dialog.show()
        }


        im2?.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_box)
            val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
            strImmagine2 = immagini?.get(2)
            storag = Firebase.storage.reference.child("images/$strImmagine2")
            storag.downloadUrl.addOnSuccessListener { url ->
                if (applicationContext != null) {
                    Glide.with(applicationContext).load(url).into(dialogImage)
                }
            }
            dialog.show()
        }

        im3?.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_box)
            val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
            strImmagine3 = immagini?.get(3)
            storag = Firebase.storage.reference.child("images/$strImmagine3")
            storag.downloadUrl.addOnSuccessListener { url ->
                if (applicationContext != null) {
                    Glide.with(applicationContext).load(url).into(dialogImage)
                }
            }
            dialog.show()
        }

        im4?.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_box)
            val dialogImage = dialog.findViewById<ImageView>(R.id.imgDialog)
            strImmagine4 = immagini?.get(4)
            storag = Firebase.storage.reference.child("images/$strImmagine4")
            storag.downloadUrl.addOnSuccessListener { url ->
                if (applicationContext != null) {
                    Glide.with(applicationContext).load(url).into(dialogImage)
                }
            }
            dialog.show()
        }


        btnChat.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("idProprietario",idProprietario)
            intent.putExtra("codiceAnn",codiceAnn)
            intent.putExtra("idCurrentUser",idCurrentUser)
            intent.putExtra("emailProprietarioAnn", emailProprietarioAnn)
            intent.putExtra("nomeArticolo", nomeArticolo)
            startActivity(intent)
        }


        btnElimina.setOnClickListener {
            var ris: Boolean
            GlobalScope.launch {
                ris = FirebaseDbWrapper(applicationContext).deleteAnnuncio(
                    applicationContext,
                    codiceAnn!!
                )
                FirebaseStorageWrapper(applicationContext).deleteImgsFromStorage(applicationContext,immagini!!)
                if (ris) {
                    val intent = Intent(applicationContext, AreaPersonaleActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
        }

        btnModifica.setOnClickListener {
            val intent = Intent(applicationContext, ModificaAnnActivity::class.java)
            intent.putExtra("codiceAnn", codiceAnn)
            startActivity(intent)
        }
    }

}