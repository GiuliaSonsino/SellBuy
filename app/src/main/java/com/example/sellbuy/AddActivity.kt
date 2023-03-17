package com.example.sellbuy

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.Annuncio
import models.FirebaseDbWrapper
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddActivity: AppCompatActivity() {

    lateinit var ImageUri : Uri
    private val auth= FirebaseAuth.getInstance()
    private val database=FirebaseDatabase.getInstance()
    //private val apiKey= "AIzaSyApg-_rad6qNXIy_7_cRsiRHeATejk-u9Q"

    private var pickup: Button? = null
    private var upload: Button? = null
    val color = Color.rgb(179,238,179)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Crea Annuncio"
        setContentView(R.layout.activity_add)

        var countImg =0
        val nomeObj = findViewById<AutoCompleteTextView>(R.id.nomeAcTv)
        val descrizioneObj = findViewById<AutoCompleteTextView>(R.id.descrizioneAcTv)
        val prezzoObj = findViewById<TextInputLayout>(R.id.prezzo)
        val categorie = resources.getStringArray(R.array.categorie)
        val adapterCat = ArrayAdapter(this, R.layout.list_item, categorie)
        val autoCompleteTextViewCat = findViewById<AutoCompleteTextView>(R.id.categoriaAcTv)
        autoCompleteTextViewCat.setAdapter(adapterCat)

        val condizioni = resources.getStringArray(R.array.condizioni)
        val adapterCond = ArrayAdapter(this, R.layout.list_item, condizioni)
        val autoCompleteTextViewCond = findViewById<AutoCompleteTextView>(R.id.statoAcTv)
        autoCompleteTextViewCond.setAdapter(adapterCond)

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_bar, null)
        val message = dialogView.findViewById<TextView>(R.id.message)


        pickup = findViewById(R.id.pickUpImg)
        upload = findViewById(R.id.uploadImg)
        val imagev = findViewById<ImageView>(R.id.iv)
        val btnAggiungi = findViewById<Button>(R.id.btnCaricaAnn)


        //Create a registry to act a getContent action
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(), //here we specify that we want pick up a content
            ActivityResultCallback {
                imagev.setImageURI(it) //once the user has selected the image, I get the URI of
                // the image and I use it to set the image into the
                //imageview widget
                if(it!=null) {
                    upload!!.isEnabled=true
                    pickup!!.isEnabled=false
                }
            }
        )

        //Choose an image from image gallery and load it into ImageView widget

        pickup!!.setOnClickListener {
            if(countImg<5) {
                getImage.launch("image/*")
            }
            else {
                Toast.makeText(
                    applicationContext,
                    "Numero massimo di immagini raggiunto",
                    Toast.LENGTH_SHORT
                ).show()
                upload!!.isEnabled=false
                pickup!!.isEnabled=false
            }
        }


        var fileName: MutableList<String> = mutableListOf()
        //Upload the image in the imageview widget
        upload!!.setOnClickListener{
            // execute the progress bar
            message.text = "Uploading..."
            builder.setView(dialogView)
            builder .setCancelable(false)
            // Remove dialogView from its current parent
            val parent = dialogView.parent as ViewGroup?
            parent?.removeView(dialogView)
            var dialog = builder.create()
            dialog.show()
            //Handler().postDelayed({dialog.dismiss()}, 5000)
            val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
            val now = Date()
            val nameImg = formatter.format(now)
            fileName.add(nameImg)
            val storageReference = FirebaseStorage.getInstance().getReference("images/$nameImg")

            //Get the byte of the image shown in the ImageView widget
            imagev.isDrawingCacheEnabled = true
            imagev.buildDrawingCache()
            val bitmap = (imagev.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos)
            val data = baos.toByteArray()

            //Upload the image
            val uploadTask = storageReference.putBytes(data)
            //When the image is uploaded the dialog will be closed
            uploadTask.addOnCompleteListener {
                dialog.dismiss()
            }
            uploadTask.addOnFailureListener {
                Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                countImg +=1
                Toast.makeText(applicationContext, "Uploaded successfully", Toast.LENGTH_LONG).show()
                pickup!!.isEnabled=true
                upload!!.isEnabled=false
            }
        }

        fun checkAdd(): Boolean {
            if (nomeObj.text.toString() == "" || descrizioneObj.text.toString() == "" || fileName.size==0) {
                Toast.makeText(
                    applicationContext,
                    "Devi compilare tutti i campi",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else if (autoCompleteTextViewCat.text.toString() == "") {
                Toast.makeText(
                    applicationContext,
                    "Selezionare una categoria",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else if (autoCompleteTextViewCond.text.toString() == "") {
                Toast.makeText(
                    applicationContext,
                    "Selezionare condizioni dell'oggetto",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else if (prezzoObj.editText?.text.toString() == "") {
                Toast.makeText(
                    applicationContext,
                    "Inserire prezzo",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else {
                return true
            }
        }

/*
        val placeSearchTv= findViewById<TextView>(R.id.placeSearch_Tv)
        if(!Places.isInitialized()) {
            Places.initialize(applicationContext,apiKey) //mettere .toString
        }
        val placesClient= Places.createClient(this)

        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        placeSearchTv.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

        }

        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.map)
                    as AutocompleteSupportFragment
*/

/*
        val apiKey= getString(R.string.api_key)
        if(!Places.isInitialized()) {
            Places.initialize(applicationContext,apiKey, Locale.ITALY)
        }
        val placesClient= Places.createClient(this)
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                    as AutocompleteSupportFragment
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID,Place.Field.NAME))

        autocompleteFragment.setOnPlaceSelectedListener( object: PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val textView= findViewById<TextView>(R.id.tv1)
                val name=place.name
                val address=place.address

                val isOpenStatus:String = if(place.isOpen==true) {
                    "Open"
                }else {
                    "Closed"
                }
                val rating=place.rating
                val userRatings=place.userRatingsTotal
                textView.text="Name: $name"
            }

            override fun onError(p0: Status) {
                Log.i(TAG, "An error occurred: $p0")

            }
        })
*/

        var switchSped = false
        val simpleSwitch: Switch = findViewById(R.id.switchSpediz)
        simpleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchSped = true
            }
        }

        // Check data and go to AddActivity
        GlobalScope.launch {
            var tel = FirebaseDbWrapper(applicationContext).getUtenteFromEmail(applicationContext)
            val n = tel?.numTel

            btnAggiungi.setOnClickListener {
                if (checkAdd()) {
                    val annuncio = Annuncio(
                        nomeObj.text.toString(),
                        autoCompleteTextViewCat.text.toString(),
                        "Prova localizzazione",
                        descrizioneObj.text.toString(),
                        prezzoObj.editText?.text.toString(),
                        autoCompleteTextViewCond.text.toString(),
                        //foto.text.toString(),
                        fileName,
                        auth.currentUser?.email.toString(),
                        n!!.toLong(),
                        switchSped
                    )
                    FirebaseDbWrapper(applicationContext).creaAnnuncio(annuncio)
                    val intent = Intent(this@AddActivity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(
                        applicationContext,
                        "Annuncio creato correttamente",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}




