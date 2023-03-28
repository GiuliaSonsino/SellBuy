package com.example.sellbuy

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import models.Annuncio
import models.FirebaseDbWrapper
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*






class AddActivity: AppCompatActivity() {

    lateinit var ImageUri: Uri
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val apiKey = "AIzaSyApg-_rad6qNXIy_7_cRsiRHeATejk-u9Q"

    private var pickup: Button? = null
    private var upload: Button? = null
    val color = Color.rgb(179, 238, 179)
/*
    // Creare una variabile per l'oggetto AutocompleteSessionToken
    private lateinit var token: AutocompleteSessionToken
    // Creare una variabile per l'API di Places
    private lateinit var placesApi: PlacesClient
    // Creare una variabile per l'elenco dei suggerimenti di autocompletamento
    private var predictionsList: MutableList<AutocompletePrediction> = mutableListOf()
    private var listLocaliz : ListView? = null
*/
    private lateinit var cityEditText: EditText
    private lateinit var placesClient: PlacesClient
    private var searchSessionToken: Any? = null

    private var selectedCityName: String? = null
    private var selectedCityLatLng: Pair<Double, Double>? = null

    private lateinit var cityAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Crea Annuncio"
        setContentView(R.layout.activity_add)

        var countImg = 0
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
                if (it != null) {
                    upload!!.isEnabled = true
                    pickup!!.isEnabled = false
                }
            }
        )

        //Choose an image from image gallery and load it into ImageView widget

        pickup!!.setOnClickListener {
            if (countImg < 5) {
                getImage.launch("image/*")
            } else {
                DynamicToast.makeWarning(
                    applicationContext,
                    "Numero massimo di immagini raggiunto",
                    Toast.LENGTH_SHORT
                ).show()
                upload!!.isEnabled = false
                pickup!!.isEnabled = false
            }
        }


        val fileName: MutableList<String> = mutableListOf()
        //Upload the image in the imageview widget
        upload!!.setOnClickListener {
            // execute the progress bar
            message.text = "Uploading..."
            builder.setView(dialogView)
            builder.setCancelable(false)
            // Remove dialogView from its current parent
            val parent = dialogView.parent as ViewGroup?
            parent?.removeView(dialogView)
            val dialog = builder.create()
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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
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
                countImg += 1
                Toast.makeText(applicationContext, "Uploaded successfully", Toast.LENGTH_LONG)
                    .show()
                pickup!!.isEnabled = true
                upload!!.isEnabled = false
            }
        }

        fun checkAdd(): Boolean {
            val prezzoText: String = prezzoObj.editText?.text.toString()
            val prezzo: Double? = prezzoText.toDoubleOrNull()

            if (nomeObj.text.toString() == "" || descrizioneObj.text.toString() == "" || fileName.size == 0) {
                DynamicToast.makeWarning(
                    applicationContext,
                    "Devi compilare tutti i campi",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            else if (autoCompleteTextViewCat.text.toString() == "") {
                DynamicToast.makeWarning(
                    applicationContext,
                    "Selezionare una categoria",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            else if (autoCompleteTextViewCond.text.toString() == "") {
                DynamicToast.makeWarning(
                    applicationContext,
                    "Selezionare condizioni dell'oggetto",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            else if (prezzoObj.editText?.text.toString() == "") {
                DynamicToast.makeWarning(
                    applicationContext,
                    "Inserire prezzo",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            else if (prezzo!! > 10000.00) {
                DynamicToast.makeWarning(
                    applicationContext,
                    "Inserire prezzo non superiore a 10000€",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            else {
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
            val tel = FirebaseDbWrapper(applicationContext).getUtenteFromEmail(applicationContext)
            val n = tel?.numTel

            btnAggiungi.setOnClickListener {
                if (checkAdd()) {
                    val annuncio = Annuncio(
                        UUID.randomUUID().toString(),
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
                        switchSped,
                        false
                    )
                    DynamicToast.makeSuccess(
                        applicationContext,
                        "Annuncio creato correttamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    FirebaseDbWrapper(applicationContext).creaAnnuncio(annuncio)
                    val intent = Intent(this@AddActivity, MainActivity::class.java)
                    startActivity(intent)

                }
            }
        }







    }








/*
        // Inizializza l'API di Places
        Places.initialize(applicationContext, apiKey)

        // Inizializzare l'API di Places
        placesApi = Places.createClient(this)
        // Inizializzare l'oggetto AutocompleteSessionToken
        token = AutocompleteSessionToken.newInstance()
        // Inizializzare l'elenco dei suggerimenti di autocompletamento
        predictionsList = mutableListOf()


        listLocaliz = findViewById(R.id.listViewLocaliz)
        val cercaLoc = findViewById<AutoCompleteTextView>(R.id.locationTextView)

        // Aggiungere un listener all'EditText che gestisce l'autocompletamento
        cercaLoc.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Chiamare il metodo per ottenere i suggerimenti di autocompletamento
                getAutocompletePredictions(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

    }


    // Metodo per ottenere i suggerimenti di autocompletamento
    /*
    private fun getAutocompletePredictionsk(query: String) {
        // Creare una richiesta di autocompletamento
        val request = Places.createAutocompleteSessionRequest()
        request.query = query
        request.locationBias = LatLngBounds(LatLng(-33.880490, 151.184363), LatLng(-33.858754, 151.229596))

        // Eseguire la richiesta di autocompletamento
        val task: Task<AutocompleteSessionResponse> = placesApi.findAutocompletePredictions(request)

        // Gestire la risposta della richiesta di autocompletamento
        task.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Rimuovere i suggerimenti precedenti dall'elenco
                predictionsList.clear()

                // Aggiungere i nuovi suggerimenti all'elenco
                val predictions: AutocompleteSessionResponse = task.result!!
                for (prediction in predictions.autocompletePredictions) {
                    predictionsList.add(prediction)
                }

                // Aggiornare la vista degli autocompletamenti
                updateAutocompleteView()
            } else {
                // Gestire l'errore
            }
        }
    }
*/
    private fun getAutocompletePredictions(query: String) {
        Places.initialize(applicationContext, apiKey)

        // Inizializzare l'API di Places
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()
        val placesClient = Places.createClient(this)
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictionsList = response.autocompletePredictions
                updateAutocompleteView()
            }
            .addOnFailureListener { exception ->
                if (exception is ApiException) {
                    Log.e(TAG, "Errore durante la ricerca di previsioni di autocompletamento: " + exception.statusCode)
                }
            }
    }


    // Metodo per aggiornare la vista degli autocompletamenti
    private fun updateAutocompleteView() {
        // Creare una lista di stringhe per contenere i nomi dei luoghi
        val placeNames: MutableList<String> = mutableListOf()

        // Aggiungere i nomi dei luoghi alla lista
        for (prediction in predictionsList) {
            placeNames.add(prediction.getPrimaryText(null).toString())
        }

        // Creare un ArrayAdapter per la lista di nomi dei luoghi
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, placeNames)

        // Aggiornare la ListView degli autocompletamenti con l'ArrayAdapter
        listLocaliz!!.adapter = adapter

        // Aggiungere un listener alla ListView degli autocompletamenti
        listLocaliz!!.setOnItemClickListener { _, _, position, _ ->
            // Ottieni l'oggetto AutocompletePrediction selezionato
            val prediction = predictionsList[position]

            // Recupera le informazioni sulla posizione dal Places API usando il Place ID
            val placeId = prediction.placeId
            val placeFields = listOf(Place.Field.LAT_LNG)

            val request = FetchPlaceRequest.newInstance(placeId, placeFields)
            placesApi.fetchPlace(request).addOnSuccessListener { response ->
                // Aggiungi un Marker sulla mappa nella posizione selezionata
                val place = response.place
                val latLng = place.latLng
                /*if (latLng != null) {
                    val markerOptions = MarkerOptions().position(latLng)
                    map.addMarker(markerOptions)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }*/
            }.addOnFailureListener { exception ->
                // Gestire l'errore
            }

            // Nascondi la ListView degli autocompletamenti
            listLocaliz!!.visibility = View.GONE
        }*/

}




