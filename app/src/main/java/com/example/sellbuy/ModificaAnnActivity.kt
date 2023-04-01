package com.example.sellbuy

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.pranavpandey.android.dynamic.toasts.DynamicToast
import kotlinx.coroutines.*
import models.FirebaseDbWrapper
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*




class ModificaAnnActivity: AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private var mMap: GoogleMap? = null
    internal lateinit var mLastLocation: Location
    internal var mCurrLocationMarker: Marker? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =123
    private var currentLatLng : LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modifica_ann)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        title = "Modifica"
        val codiceAnn = intent.getStringExtra("codiceAnn")
        val catAnn = intent.getStringExtra("catAnn")
        val condAnn = intent.getStringExtra("condAnn")

        //gestione posizione attuale
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

        }
        else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if(location!=null) {
                    currentLatLng = LatLng(location.latitude,location.longitude)
                }
                else {

                }
            }
        }

        val titolo = findViewById<EditText>(R.id.edit_title)
        val descrizione = findViewById<EditText>(R.id.edit_description)
        val prezzo = findViewById<EditText>(R.id.edit_price)
        val spedizione = findViewById<Switch>(R.id.edit_switchSpediz)
        var sped: Boolean? = null
        var localizzazione = findViewById<TextView>(R.id.luogo_inserito)
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
                val stringaCoordinate = currentAnnuncio.localizzazione
                val coordinate = stringToLatLng(stringaCoordinate!!)
                val citta = getCityName(applicationContext,coordinate!!)
                localizzazione.text=citta
            }
        }

        var countLoc=0
        val lat = 75.122808
        val lng = 0.106208
        var loc : LatLng? = null
        val btnModificaLocalizzazione = findViewById<Button>(R.id.btn_modifica_localizzazione)
        btnModificaLocalizzazione.setOnClickListener {
            countLoc +=1
            loc = saveLocation()

            if(loc == LatLng(lat, lng) ) {
                countLoc=0
            }
            else {
                Toast.makeText(this, "Localizzazione salvata", Toast.LENGTH_SHORT).show()
                val cittaAggiornata = getCityName(applicationContext,loc!!)
                localizzazione.text=cittaAggiornata
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
                val stringaLuogo = localizzazione.text.toString()
                val coordinateLuogo = getLatLngFromCityName(applicationContext,stringaLuogo)
                val s = sped()
                FirebaseDbWrapper(applicationContext).modificaInfoAnnuncio(
                    applicationContext,
                    codiceAnn!!,
                    tit,
                    desc,
                    prez,
                    cat,
                    stato,
                    s,
                    coordinateLuogo.toString(),
                )
            }
            finish()
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(intent)
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

    fun getLatLngFromCityName(context: Context, cityName: String): LatLng? {
        val geocoder = Geocoder(context, Locale.getDefault())
        var latLng: LatLng? = null

        try {
            val addresses = geocoder.getFromLocationName(cityName, 1)
            if (addresses!!.isNotEmpty()) {
                latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
            }
        } catch (e: IOException) {
            Toast.makeText(context, "Errore di rete", Toast.LENGTH_SHORT).show()
        }

        return latLng
    }



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

            if(addressList!!.isEmpty()) {
                DynamicToast.makeWarning(
                    this,
                    "Luogo non trovato",
                    Toast.LENGTH_LONG
                ).show()
            }

            else {
                val address = addressList!![0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
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
            if(addressList!!.isEmpty()) {
                DynamicToast.makeWarning(this, "Luogo non trovato, è stata inserita la tua posizione attuale", Toast.LENGTH_LONG).show()
                latLng = currentLatLng
                mMap!!.addMarker(MarkerOptions().position(latLng!!).title(location))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }

            else {
                val address = addressList!![0]
                latLng = LatLng(address.latitude, address.longitude)
                mMap!!.addMarker(MarkerOptions().position(latLng).title(location))
                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
        return latLng!!
    }

}
