package com.example.sellbuy
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.sellbuy.R
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
/*
class esperimento : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var citySearch: EditText
    private lateinit var cityList: ListView

    private lateinit var googleMap: GoogleMap
    private lateinit var cities: List<String>

    private var selectedCity: String? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    private lateinit var placesClient: PlacesClient
    //private lateinit var autoCompleteAdapter: PlaceAutocompleteAdapter
    private var selectedPlace: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prova)

        // Initialize views
        mapView = findViewById(R.id.map_view)
        citySearch = findViewById(R.id.city_search)
        cityList = findViewById(R.id.city_list)

        // Initialize Google Maps
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)


        // Initialize Places SDK
        Places.initialize(applicationContext, "YOUR_API_KEY")
        placesClient = Places.createClient(this)

        // Set up auto complete adapter
        val autocompleteFragment =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setHint("Search city...")
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                selectedPlace = place
                selectedPlace?.latLng?.let { latLng ->
                    selectedLatitude = latLng.latitude
                    selectedLongitude = latLng.longitude
                    updateMap()
                }
            }

            override fun onError(status: Status) {
                Log.e("AutocompleteFragment", "Error getting place: ${status.statusMessage}")
            }
        })




        // Set up city search
        citySearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter cities list by input text
                val filteredCities =
                    cities.filter { it.startsWith(s.toString(), ignoreCase = true) }

                // Update city list view
                val adapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    filteredCities
                )
                cityList.adapter = adapter

                // Set up city list item click listener
                cityList.setOnItemClickListener { _, _, position, _ ->
                    // Get selected city
                    selectedCity = filteredCities[position]

                    // Hide keyboard
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(citySearch.windowToken, 0)

                    // Get selected city's coordinates and update map
                    val geocoder = Geocoder(applicationContext)
                    val addressList = geocoder.getFromLocationName(selectedCity!!, 1)
                    if (addressList != null) {
                        if (addressList.isNotEmpty()) {
                            val address = addressList?.get(0)
                            selectedLatitude = address.latitude
                            selectedLongitude = address.longitude
                            updateMap()
                        }
                    }

            }
        }
    }

}

}
*/
class esperimento : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var citySearch: EditText
    private lateinit var cityList: ListView

    private lateinit var googleMap: GoogleMap

    private var selectedCity: String? = null
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null

    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prova)

        // Initialize views
        mapView = findViewById(R.id.map_view)
        citySearch = findViewById(R.id.city_search)
        cityList = findViewById(R.id.city_list)

        // Initialize Google Maps
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Initialize Places SDK
        Places.initialize(applicationContext, "AIzaSyApg-_rad6qNXIy_7_cRsiRHeATejk-u9Q")
        placesClient = Places.createClient(this)

        // Set up city search
        citySearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Use Places SDK to get suggestions for cities
                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(s.toString())
                    .setTypeFilter(TypeFilter.CITIES)
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                        // Convert response to list of city names
                        val cityNames = response.autocompletePredictions.map { it.getPrimaryText(null).toString() }

                        // Update city list view
                        val adapter = ArrayAdapter(
                            applicationContext,
                            android.R.layout.simple_list_item_1,
                            cityNames
                        )
                        cityList.adapter = adapter

                        // Set up city list item click listener
                        cityList.setOnItemClickListener { _, _, position, _ ->
                            // Get selected city
                            selectedCity = cityNames[position]

                            // Hide keyboard
                            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(citySearch.windowToken, 0)

                            // Get selected city's coordinates and update map
                            val geocoder = Geocoder(applicationContext)
                            val addressList = geocoder.getFromLocationName(selectedCity!!, 1)
                            if (addressList != null) {
                                if (addressList.isNotEmpty()) {
                                    val address = addressList[0]
                                    selectedLatitude = address.latitude
                                    selectedLongitude = address.longitude
                                    updateMap()
                                }
                            }
                        }
                    }
                    .addOnFailureListener { exception: Exception ->
                        Log.e("AutocompleteFragment", "Error getting city predictions: ${exception.message}")
                    }
            }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateMap()
        mapView.onResume()
    }

    private fun updateMap() {
        // Update map marker and camera position
        if (selectedLatitude != null && selectedLongitude != null) {
            val location = LatLng(selectedLatitude!!, selectedLongitude!!)
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(location).title(selectedCity))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        }
    }
}
