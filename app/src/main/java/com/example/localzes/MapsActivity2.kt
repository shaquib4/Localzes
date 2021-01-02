package com.example.localzes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps2.*
import util.ConnectionManager
import java.util.*

class MapsActivity2 : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraIdleListener {


    private lateinit var map: GoogleMap
    private lateinit var auth: FirebaseAuth
    var firebaseUser: FirebaseUser? = null
    private val LOCATION_PERMISSION_REQUEST = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var userDatabase: DatabaseReference

    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            map.setOnCameraMoveListener(this)
            map.setOnCameraMoveStartedListener(this)
            map.setOnCameraIdleListener(this)
            locationRequest = LocationRequest()
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val settingsClient = LocationServices.getSettingsClient(this)
            val task = settingsClient.checkLocationSettings(builder.build())
            task.addOnSuccessListener {
                getLocationUpdates()
                startLocationUpdates()
            }
                .addOnFailureListener { e ->
                    if (e is ResolvableApiException)
                        try {
                            e.startResolutionForResult(this@MapsActivity2, 51)
                        } catch (e1: IntentSender.SendIntentException) {
                            e1.printStackTrace()
                        }
                }
        } else
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true
                map.setOnCameraMoveListener(this)
                map.setOnCameraMoveStartedListener(this)
                map.setOnCameraIdleListener(this)
                locationRequest = LocationRequest()
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                val settingsClient = LocationServices.getSettingsClient(this)
                val task = settingsClient.checkLocationSettings(builder.build())
                task.addOnSuccessListener {
                    getLocationUpdates()
                    startLocationUpdates()
                }
                    .addOnFailureListener { e ->
                        if (e is ResolvableApiException) {
                            try {
                                e.startResolutionForResult(this@MapsActivity2, 51)
                            } catch (e1: IntentSender.SendIntentException) {
                                e1.printStackTrace()
                            }
                        }
                    }
            } else {
                Toast.makeText(
                    this,
                    "User has not granted location access permission",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = FirebaseAuth.getInstance()
        retryMapS.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)){
                rl_mapS.visibility= View.VISIBLE
                rl_retryMapS.visibility=View.GONE
                this.recreate()
            }else{
                rl_mapS.visibility= View.GONE
                rl_retryMapS.visibility=View.VISIBLE
            }
        }
        SaveAddress_seller.setOnClickListener {
            when {
                edtLocality_seller.text.toString().isEmpty() -> {
                    edtLocality_seller.error = "Please Enter Your Locality"
                    return@setOnClickListener
                }
                edtNearestLandmark_seller.text.toString().isEmpty() -> {
                    edtNearestLandmark_seller.error = "Please Enter Your Landmark"
                    return@setOnClickListener
                }
                else -> {
                    if (ConnectionManager().checkConnectivity(this)){
                        rl_mapS.visibility= View.VISIBLE
                        rl_retryMapS.visibility=View.GONE
                    val name = intent.getStringExtra("name")
                    val email = intent.getStringExtra("email")
                    val address = btnmapseller.text.toString()
                    val city = txtCity_seller.text.toString()
                    val state = txtState_seller.text.toString()
                    val country = txtCountry_seller.text.toString()
                    val pinCode = txtPincode_seller.text.toString()
                    val locality = txtLocality_seller.text.toString()
                    val locality2 = edtLocality_seller.text.toString()
                    val nearestLandmark = edtNearestLandmark_seller.text.toString()
                    val houseNo = HouseNo_seller.text.toString()
                    val intent = Intent(this, SellerShop_detail::class.java)
                    intent.putExtra("name", name)
                    intent.putExtra("email", email)
                    intent.putExtra("address", address)
                    intent.putExtra("city", city)
                    intent.putExtra("state", state)
                    intent.putExtra("country", country)
                    intent.putExtra("pinCode", pinCode)
                    intent.putExtra("locality", locality)
                    intent.putExtra("locality2", locality2)
                    intent.putExtra("nearestLandmark", nearestLandmark)
                    intent.putExtra("HouseNo", houseNo)
                    startActivity(intent)
                    finish()}else{
                        rl_mapS.visibility= View.GONE
                        rl_retryMapS.visibility=View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == Activity.RESULT_OK) {
                getLocationUpdates()
                startLocationUpdates()
            }
        }
    }
    public fun getLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest.interval = 30000
        locationRequest.fastestInterval = 20000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.lastLocation


                    val lot = location.latitude

                    val long = location.longitude
                    val geocoder: Geocoder
                    val addresses: List<Address>
                    geocoder = Geocoder(applicationContext, Locale.getDefault())
                    try {
                        addresses = geocoder.getFromLocation(lot, long, 1)
                        val address: String = addresses[0].getAddressLine(0)


                        val city: String = addresses[0].subAdminArea
                        val state: String = addresses[0].adminArea
                        val pinCode: String = addresses[0].postalCode
                        val country: String = addresses[0].countryName
                        val locality: String = addresses[0].locality
                        btnmapseller.text = "$locality, $state($city), $pinCode $country"
                        txtCity_seller.text = city
                        txtState_seller.text = state
                        txtPincode_seller.text = pinCode
                        txtLocality_seller.text = locality
                        txtCountry_seller.text = country
                        City_seller.text = "($city),"
                        State_seller.text = state
                        Pincode_seller.text = " $pinCode"

                        Locality_bold_seller.text = locality
                        localit_seller.text = "$locality,"
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    fusedLocationClient.removeLocationUpdates(locationCallback)




                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        val markerOptions = MarkerOptions().position(latLng)
                        //   map.addMarker(markerOptions)
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                    }
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getLocationAccess()

        /* val zoom=15f

         val sydney = LatLng(26.7606, 83.3732)
         map.addMarker(MarkerOptions().position(sydney).title("Gorakhpur , India").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
         map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,zoom))*/
    }

    override fun onLocationChanged(location: Location?) {
        val geocoder: Geocoder

        val addresses: List<Address>

        try {
            geocoder = Geocoder(applicationContext, Locale.getDefault())
            addresses = geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1)
            val address: String = addresses[0].getAddressLine(0)
            val city: String = addresses[0].subAdminArea
            val state: String = addresses[0].adminArea
            val pinCode: String = addresses[0].postalCode
            val country: String = addresses[0].countryName
            val locality: String = addresses[0].locality

            btnmapseller.text = "$locality, $state($city), $pinCode $country"
            txtCity_seller.text = city
            txtState_seller.text = state
            txtPincode_seller.text = pinCode
            txtLocality_seller.text = locality
            txtCountry_seller.text = country
            City_seller.text = "($city),"
            State_seller.text = state
            Pincode_seller.text = " $pinCode"

            Locality_bold_seller.text = locality
            localit_seller.text = "$locality,"


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCameraMove() {

    }

    override fun onCameraMoveStarted(p0: Int) {

    }

    override fun onCameraIdle() {
        val geocoder: Geocoder

        val addresses: List<Address>
        geocoder = Geocoder(applicationContext, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                map!!.cameraPosition.target.latitude,
                map!!.cameraPosition.target.longitude,
                1
            )
            val address: String = addresses[0].getAddressLine(0)
            val city: String = addresses[0].subAdminArea
            val state: String = addresses[0].adminArea
            val pinCode: String = addresses[0].postalCode
            val country: String = addresses[0].countryName
            val locality: String = addresses[0].locality

            btnmapseller.text = "$locality, $state($city), $pinCode $country"
            txtCity_seller.text = city
            txtState_seller.text = state
            txtPincode_seller.text = pinCode
            txtLocality_seller.text = locality
            txtCountry_seller.text = country
            City_seller.text = "($city),"
            State_seller.text = state
            Pincode_seller.text = " $pinCode"

            Locality_bold_seller.text = locality
            localit_seller.text = "$locality,"

        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val intent=Intent(this,Registerdetails_seller::class.java)
        startActivity(intent)
        finish()
    }

}