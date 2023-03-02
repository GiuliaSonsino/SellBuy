package com.example.sellbuy

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    private var pickup: Button? = null
    private var upload: Button? = null
    val color = Color.rgb(179,238,179)
    /*
    private var progressBar: ProgressBar? = null
    private var i = 0
    private val handler = android.os.Handler()
    private var txtView: TextView? = null
    */

    /*
    private val AUTOCOMPLETE_REQUEST_CODE = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i(TAG, "Place: ${place.name}, ${place.id}")
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Crea Annuncio"
        setContentView(R.layout.activity_add)

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

        //progressBar = findViewById<ProgressBar>(R.id.progress_Bar) as ProgressBar
        //txtView = findViewById<TextView>(R.id.text_view)

        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_bar, null)
        val message = dialogView.findViewById<TextView>(R.id.message)




        pickup = findViewById(R.id.pickUpImg)
        upload = findViewById(R.id.uploadImg)
        val imagev = findViewById<ImageView>(R.id.iv)


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


        //Execute the action defined above that is:
        //Pick up an image from image gallery and load it into ImageView widget
        pickup!!.setOnClickListener{
            getImage.launch("image/*") //here we specify the type of content we want
           /* upload!!.isClickable=true
            upload!!.setBackgroundColor(color)
            pickup!!.isClickable=false
            pickup!!.setBackgroundColor(Color.WHITE)*/

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
            Handler().postDelayed({dialog.dismiss()}, 5000)



            /*
            pickup!!.visibility=View.INVISIBLE
            upload!!.visibility=View.INVISIBLE
            progressBar!!.visibility = View.VISIBLE
            txtView!!.visibility=View.VISIBLE
            i = progressBar!!.progress
            Thread(Runnable {
                while (i < 100) {
                    i += 1
                    handler.post(Runnable {
                        progressBar!!.progress = i
                        txtView!!.text = "Caricamento..." + i.toString() + "/" + progressBar!!.max
                    })
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                progressBar!!.visibility = View.INVISIBLE
                txtView!!.visibility=View.INVISIBLE

            }).start()

            pickup!!.visibility=View.VISIBLE
            upload!!.visibility=View.VISIBLE
        */

            //The filename is set by using current hour and day
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

            //Check if everything is ok
            uploadTask.addOnFailureListener {
                Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Toast.makeText(applicationContext, "Uploaded successfully", Toast.LENGTH_LONG).show()
                /*upload!!.isClickable=false
                upload!!.setBackgroundColor(Color.WHITE)
                pickup!!.isClickable=true
                pickup!!.setBackgroundColor(color)*/
                pickup!!.isEnabled=true
                upload!!.isEnabled=false
            }
        }






        val btnAggiungi = findViewById<Button>(R.id.btnCaricaAnn)

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
        val selezionaImmagine = findViewById<ImageButton>(R.id.selezionaImg)


        selezionaImmagine.setOnClickListener {
            ImagePicker.with(this@AddActivity)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .galleryMimeTypes(  //Exclude gif images
                    mimeTypes = arrayOf(
                        "image/png",
                        "image/jpg",
                        "image/jpeg"
                    )
                )
                .start()
        }
*/
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
        /*
        val user= FirebaseAuth.getInstance().currentUser
        user?.getIdToken(true)?.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val idToken=task.result?.token
                val claims=FirebaseAuth.getInstance().verify
            }
        }
*/

        var switchSped = false
        val simpleSwitch: Switch = findViewById(R.id.switchSpediz)
        simpleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchSped = true
            }
        }

/*
        val user=FirebaseAuth.getInstance().currentUser
        val email=user?.email
        val id=auth.uid
        var telefono=""
        val myref=database.getReference()
        myref.child("id_"+id.toString().replace(".","_")+"/numTel")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value= dataSnapshot.getValue(String::class.java)
                    telefono=value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
*/

GlobalScope.launch {

    var tel = FirebaseDbWrapper(applicationContext).getUtenteFromEmail(applicationContext)
    val n = tel?.numTel
    Log.d(TAG, "il numerooooo $n")






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
        } else {
            Toast.makeText(
                applicationContext,
                "Annuncio creato non correttamente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}


    }

}




