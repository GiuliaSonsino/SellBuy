package com.example.sellbuy

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Build
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*






class AddActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private val auth = FirebaseAuth.getInstance()
    private var pickup: Button? = null
    private var upload: Button? = null
    val color = Color.rgb(179, 238, 179)
    var countLoc = 0


    private var mMap: GoogleMap? = null
    internal lateinit var mLastLocation: Location
    internal var mCurrLocationMarker: Marker? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Crea Annuncio"
        setContentView(R.layout.activity_add)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
        val lat = 75.122808
        val lng = 0.106208

        var loc : LatLng? = null
        val salvaLoc = findViewById<Button>(R.id.btn_salva_localizzazione)
        salvaLoc.setOnClickListener {
            countLoc +=1
            loc = saveLocation()

            if(loc == LatLng(lat, lng) ) {
                countLoc=0
            }
            else {
                Toast.makeText(this, "Localizzazione salvata", Toast.LENGTH_SHORT).show()
            }

        }

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
            else if(countLoc==0) {
                DynamicToast.makeWarning(
                    applicationContext,
                    "Inserire localizzazione e salvare",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            else {
                return true
            }
        }


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
                        loc.toString(),
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


    //inizio gestione mappa

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ){
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        }else{
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
    }

    protected fun buildGoogleApiClient(){
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mCurrLocationMarker != null){
            mCurrLocationMarker!!.remove()
        }

        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.moveCamera(CameraUpdateFactory.zoomTo(11f))

        if (mGoogleApiClient != null){
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onConnected(p0: Bundle?) {

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


    fun searchLocation(view: View){
        val locationSearch: EditText = findViewById(R.id.et_search)
        var location: String
        location = locationSearch.text.toString().trim()
        var addressList: List<Address>? = null

        if (location == null || location == ""){
            Toast.makeText(this, "provide location", Toast.LENGTH_SHORT).show()
        }else{
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
            }catch (e: IOException){
                e.printStackTrace()
            }

            val address = addressList!![0]
            val latLng = LatLng(address.latitude, address.longitude)
            mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }


    private fun saveLocation(): LatLng {
        var latLng: LatLng? = null
        val locationSearch: EditText = findViewById(R.id.et_search)
        val location: String
        location = locationSearch.text.toString().trim()
        var addressList: List<Address>? = null

        if (location == null || location == ""){
            DynamicToast.makeWarning(this, "Inserisci luogo", Toast.LENGTH_SHORT).show()
            //valori di default nel caso non selezionasse un luogo
            val lat = 75.122808
            val lng = 0.106208
            latLng = LatLng(lat, lng)
        }else{
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
            }catch (e: IOException){
                e.printStackTrace()
            }

            val address = addressList!![0]
            latLng = LatLng(address.latitude, address.longitude)
            mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
        return latLng!!
    }

}




