package com.example.sellbuy

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.*
import models.Annuncio
import models.FirebaseDbWrapper
import models.FirebaseStorageWrapper
import models.Recensione
import java.io.IOException
import java.util.*

class AnnuncioActivity : AppCompatActivity() {

    var immagini: MutableList<String>? = mutableListOf()
    var cat:String?=null
    var cond: String? = null

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
        val btnAcquista= findViewById<Button>(R.id.btnAcquista)
        val numTel = findViewById<TextView>(R.id.tv_num_tel)
        numTel.setOnClickListener {
            val phoneUri = Uri.parse("tel:${numTel.text}")
            val phoneIntent = Intent(Intent.ACTION_DIAL, phoneUri)
            startActivity(phoneIntent)
        }


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
            numTel.text = ann.numTel.toString()
            val descrizione = findViewById<TextView>(R.id.tv_description)
            descrizione.text = ann.descrizione
            val prezzo = findViewById<TextView>(R.id.tv_price)
            prezzo.text = ann.prezzo
            val venduto = findViewById<TextView>(R.id.tv_venduto)
            if (ann.venduto) {
                venduto.text = "VENDUTO"
            }
            val luogo = findViewById<TextView>(R.id.tv_luogo)
            val stringaCoordinate = ann.localizzazione
            val coordinate = stringToLatLng(stringaCoordinate!!)
            val nomeCitta=getCityName(applicationContext,coordinate!!)
            luogo.text = nomeCitta
            val condizione = findViewById<TextView>(R.id.tv_condition)
            condizione.text = ann.stato
            cond = ann.stato
            val categoria = findViewById<TextView>(R.id.tv_category)
            categoria.text = ann.categoria
            cat = ann.categoria
            val spedizione = findViewById<TextView>(R.id.tv_spedizione)
            if (ann.spedizione) {
                spedizione.text = "è disposto"
            } else {
                spedizione.text = "non è disposto"
            }

            emailProprietarioAnn = ann.email
            nomeArticolo = ann.nome
            idProprietario = FirebaseDbWrapper(applicationContext).getIdUtenteFromEmail(
                applicationContext,
                emailProprietarioAnn!!
            )

            immagini = ann.foto
            strMainImm = immagini?.get(0)
            storag = Firebase.storage.reference.child("images/$strMainImm")
            storag.downloadUrl.addOnSuccessListener { url ->
                if (applicationContext != null) {
                    Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                        .override(500, 500).into(mainImmagine)
                }
            }
            if (immagini?.size!! >= 2) {
                strImmagine1 = immagini?.get(1)
                storag = Firebase.storage.reference.child("images/$strImmagine1")
                storag.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                            .override(500, 500).into(im1)
                    }
                }
            } else {
                View.INVISIBLE.also { im1.visibility = it }
            }

            if (immagini?.size!! >= 3) {
                strImmagine2 = immagini?.get(2)
                storag = Firebase.storage.reference.child("images/$strImmagine2")
                storag.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                            .override(500, 500).into(im2)
                    }
                }
            } else {
                View.INVISIBLE.also { im2.visibility = it }
            }

            if (immagini?.size!! >= 4) {
                strImmagine3 = immagini?.get(3)
                storag = Firebase.storage.reference.child("images/$strImmagine3")
                storag.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                            .override(500, 500).into(im3)
                    }
                }
            } else {
                View.INVISIBLE.also { im3.visibility = it }
            }

            if (immagini?.size!! >= 5) {
                strImmagine4 = immagini?.get(4)
                storag = Firebase.storage.reference.child("images/$strImmagine4")
                storag.downloadUrl.addOnSuccessListener { url ->
                    if (applicationContext != null) {
                        Glide.with(applicationContext).load(url).skipMemoryCache(true) // Opzione 2
                            .override(500, 500).into(im4)
                    }
                }
            } else {
                View.INVISIBLE.also { im4.visibility = it }
            }


            //handle visibility button
            if (em.equals(ann.email)) {
                //btnElimina.visibility= View.VISIBLE
                View.INVISIBLE.also { btnAcquista.visibility = it }
                View.INVISIBLE.also { btnChat.visibility = it }
            } else {
                View.VISIBLE.also { btnAcquista.visibility = it }
                View.VISIBLE.also { btnChat.visibility = it }
                if (ann.venduto) {
                    btnAcquista.isClickable = false
                    runOnUiThread {
                        DynamicToast.makeError(
                            applicationContext,
                            "Annuncio già venduto",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }
        }


        val dialog = Dialog(this)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT

        mainImmagine?.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_box)
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


        btnAcquista.setOnClickListener {
            GlobalScope.launch {
                val acquirente = FirebaseDbWrapper(applicationContext).getUtenteFromEmail(applicationContext)
                val venditore = FirebaseDbWrapper(applicationContext).getUtenteFromCodice(applicationContext,idProprietario!!)
                val an = FirebaseDbWrapper(applicationContext).getAnnuncioFromCodice(applicationContext, codiceAnn!!)
                if(acquirente!!.credito >= an.prezzo.toDouble()) {
                    //creo un oggetto Recensione
                    var recensione = Recensione(FirebaseAuth.getInstance().currentUser?.email,0,"",false,venditore.email,0,"",false,codiceAnn)
                    FirebaseDbWrapper(applicationContext).creaRecensione(recensione)
                    val soldiRimasti = acquirente.credito - an.prezzo.toDouble()
                    val soldiAggiunti = venditore.credito + an.prezzo.toDouble()
                    FirebaseDbWrapper(applicationContext).modificaCreditoUtente(applicationContext,idCurrentUser!!,soldiRimasti)
                    FirebaseDbWrapper(applicationContext).modificaCreditoUtente(applicationContext,idProprietario!!,soldiAggiunti)
                    val intent = Intent(applicationContext, AcquistoAvvenuto::class.java)
                    intent.putExtra("soldiRimasti", soldiRimasti.toString())
                    intent.putExtra("venditore", venditore.email)
                    intent.putExtra("codiceAnn", codiceAnn)
                    startActivity(intent)
                    finish()
                }
                else {
                    runOnUiThread {
                        DynamicToast.makeError(
                            applicationContext,
                            "Credito non sufficiente",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }
    }


    fun stringToLatLng(inputString: String): LatLng? {
        val regex = ".*\\(([^,]*),([^)]*)\\).*".toRegex()
        val matchResult = regex.find(inputString)
        if (matchResult != null && matchResult.groupValues.size >= 3) {
            val lat = matchResult.groupValues[1].toDoubleOrNull()
            val lng = matchResult.groupValues[2].toDoubleOrNull()
            if (lat != null && lng != null) {
                return LatLng(lat, lng)
            }
        }
        return null
    }


    fun getCityName(context: Context, latLng: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var cityName = ""

        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                cityName = addresses[0].locality
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Errore di rete", Toast.LENGTH_SHORT).show()
        }

        return cityName
    }



    private fun showVoteDialog(codiceAnn :String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Vota il servizio e lascia una recensione")
        val ratingOptions = arrayOf("1", "2", "3", "4", "5")
        var selectedRating = 0
        builder.setSingleChoiceItems(ratingOptions, selectedRating) { dialog, which ->
            selectedRating = which
        }

        val input = EditText(this)
        input.hint="Scrivi qui la recensione"
        builder.setView(input)

        builder.setPositiveButton("Salva") { dialog, which ->
            val rating = ratingOptions[selectedRating]
            val review = input.text.toString()
            GlobalScope.launch {
                FirebaseDbWrapper(applicationContext).modificaRecensioneVenditore(
                    applicationContext,
                    codiceAnn,
                    rating.toInt(),
                    review,
                    true
                )
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        builder.setNegativeButton("Non voglio votare") { dialog, which ->
            GlobalScope.launch {
                FirebaseDbWrapper(applicationContext).modificaRecensioneVenditore(
                    applicationContext,
                    codiceAnn,
                    0,
                    "",
                    false
                )
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        builder.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val firebaseAuth = FirebaseAuth.getInstance()
        val codiceAnn = intent.getStringExtra("codice")
        when (item.itemId) {
            R.id.logout -> {
                firebaseAuth.signOut()
                val intent = Intent(applicationContext, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.home -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.addImg -> {
                if(immagini!!.size==5) {
                    DynamicToast.makeError(applicationContext, "Impossibile aggiungere più di 5 immagini", Toast.LENGTH_LONG)
                        .show()
                }
                else {
                    val intent = Intent(applicationContext, AggiungiFotoActivity::class.java)
                    intent.putExtra("codiceAnn", codiceAnn)
                    startActivity(intent)
                }
            }
            R.id.eliminaImg -> {
                if(immagini!!.size==1) {
                    DynamicToast.makeError(applicationContext, "Impossibile eliminare tutte le immagini", Toast.LENGTH_LONG)
                        .show()
                }
                else {
                    val intent = Intent(applicationContext, EliminaFotoActivity::class.java)
                    intent.putExtra("codiceAnn", codiceAnn)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            R.id.modificaInfo -> {
                val intent = Intent(applicationContext, ModificaAnnActivity::class.java)
                intent.putExtra("codiceAnn", codiceAnn)
                intent.putExtra("catAnn", cat)
                intent.putExtra("condAnn", cond)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.segnaVenduto -> {
                var bisognaRecensire : Boolean =false
                CoroutineScope(Dispatchers.IO).launch {
                    FirebaseDbWrapper(applicationContext).segnaComeVenduto(
                        applicationContext,
                        codiceAnn!!
                    )
                    bisognaRecensire = FirebaseDbWrapper(applicationContext).bisognaRecensire(
                        applicationContext,
                        codiceAnn!!
                    )
                    withContext(Dispatchers.Main) {
                        if (bisognaRecensire) {
                            showVoteDialog(codiceAnn)
                        }
                        else {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }
                }

                /*
                GlobalScope.launch {
                    FirebaseDbWrapper(applicationContext).segnaComeVenduto(applicationContext,codiceAnn!!)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    showVoteDialog()
                }*/
                Toast.makeText(applicationContext, "Annuncio segnato come venduto", Toast.LENGTH_LONG).show()
            }

            R.id.elimina -> {
                var ris: Boolean
                GlobalScope.launch {
                    ris = FirebaseDbWrapper(applicationContext).deleteAnnuncio(
                        applicationContext,
                        codiceAnn!!
                    )
                    FirebaseStorageWrapper(applicationContext).deleteImgsFromStorage(applicationContext,immagini!!)
                    if (ris) {
                        finish()
                        val intent = Intent(applicationContext, AreaPersonaleActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val codiceAnn = intent.getStringExtra("codice")
        CoroutineScope(Dispatchers.IO).launch {
            val bool = FirebaseDbWrapper(applicationContext).isProprietarioAnnuncio(applicationContext,codiceAnn!!)
            withContext(Dispatchers.Main) {
                if(!bool) {
                    menuInflater.inflate(R.menu.menu_to_home, menu)
                }
                else {
                    menuInflater.inflate(R.menu.menu_proprietario, menu)
                }
            }
        }
        return true
    }



}