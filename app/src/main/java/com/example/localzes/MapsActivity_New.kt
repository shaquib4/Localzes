package com.example.localzes

import android.Manifest
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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps2.*
import kotlinx.android.synthetic.main.activity_maps__new.*
import util.ConnectionManager
import java.io.IOException
import java.lang.IndexOutOfBoundsException
import java.util.*

class MapsActivity_New : AppCompatActivity(), OnMapReadyCallback, LocationListener,
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
    var timestamp: String = ""
    var maxId: Int = 0

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
                            e.startResolutionForResult(this@MapsActivity_New, 51)
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
                        if (e is ResolvableApiException)
                            try {
                                e.startResolutionForResult(this@MapsActivity_New, 51)
                            } catch (e1: IntentSender.SendIntentException) {
                                e1.printStackTrace()
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
        setContentView(R.layout.activity_maps__new)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        timestamp = System.currentTimeMillis().toString()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        retryMapN.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)){
               rl_mapN.visibility= View.VISIBLE
                rl_retryMapN.visibility=View.GONE
                this.recreate()
            }else{
                rl_mapN.visibility= View.GONE
                rl_retryMapN.visibility=View.VISIBLE
            }
        }
        userDatabase =
            FirebaseDatabase.getInstance().reference.child("users").child(uid).child("address")

       userDatabase!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    maxId = snapshot.childrenCount.toInt()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
        SaveAddress_new.setOnClickListener {
            when {
                edtMobileNumber.text.toString().isEmpty() -> {
                    edtMobileNumber.error = "Please Enter Your Mobile Number"
                    return@setOnClickListener
                }
                edtLocality_newr.text.toString().isEmpty() -> {
                    edtLocality_newr.error = "Please Provide Your Locality Details"
                    return@setOnClickListener
                }
                edtNearestLandmark_new.text.toString().isEmpty() -> {
                    edtNearestLandmark_new.error =
                        "Please Provide Your Nearest Landmark for easy Delivery"
                    return@setOnClickListener
                }
                else -> {
                    if (ConnectionManager().checkConnectivity(this)){
                        rl_mapN.visibility= View.VISIBLE
                        rl_retryMapN.visibility=View.GONE
                    val address = btnmap_new.text.toString()
                    val city = txtCity_new.text.toString()
                    val state = txtState_new.text.toString()
                    val country = txtCountry_new.text.toString()
                    val pinCode = txtPincode_new.text.toString()
                    val locality = txtLocality_new.text.toString()
                    val locality2 = edtLocality_newr.text.toString()
                    val nearestLandmark = edtNearestLandmark_new.text.toString()
                    val houseNo = HouseNo_new.text.toString()
                    val mobNo = edtMobileNumber.text.toString()
                    userDatabase =
                        FirebaseDatabase.getInstance().reference.child("users").child(uid)
                            .child("address")
                    val userMap = HashMap<String, Any>()
                    userMap["address"] = address
                    userMap["city"] = city
                    userMap["state"] = state
                    userMap["country"] = country
                    userMap["pinCode"] = pinCode
                    userMap["locality"] = locality
                    userMap["locality2"] = locality2
                    userMap["houseBlock"] = houseNo
                    userMap["nearestLandmark"] = nearestLandmark
                    userMap["mobileNo"] = mobNo
                    userMap["id"] = timestamp

                    userDatabase.child(timestamp).setValue(userMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                startActivity(Intent(this, Accounts::class.java))
                                finish()
                            }


                        }
                }else{
                        rl_mapN.visibility= View.GONE
                        rl_retryMapN.visibility=View.VISIBLE
                    }
                }
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

                    try {
                        geocoder = Geocoder(applicationContext, Locale.getDefault())
                        addresses = geocoder.getFromLocation(lot, long, 1)


                        val address: String = addresses[0].getAddressLine(0)
                        val city: String = addresses[0].subAdminArea
                        val state: String = addresses[0].adminArea
                        val pinCode: String = addresses[0].postalCode
                        val country: String = addresses[0].countryName
                        val locality: String = addresses[0].locality

                        btnmap_new.text = "$locality, $state($city), $pinCode $country"
                        txtCity_new.text = city
                        txtState_new.text = state
                        txtPincode_new.text = pinCode
                        txtLocality_new.text = locality
                        txtCountry_new.text = country
                        City_new.text = "($city),"
                        State_new.text = state
                        Pincode_new.text = " $pinCode"

                        Locality_bold_new.text = locality
                        localit_new.text = "$locality,"


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    fusedLocationClient.removeLocationUpdates(locationCallback)




                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        val markerOptions = MarkerOptions().position(latLng)
                        map.addMarker(markerOptions)
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
        geocoder = Geocoder(applicationContext, Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(location!!.latitude, location!!.longitude, 1)
            val address: String = addresses[0].getAddressLine(0)
            val city: String = addresses[0].subAdminArea
            val state: String = addresses[0].adminArea
            val pinCode: String = addresses[0].postalCode
            val country: String = addresses[0].countryName
            val locality: String = addresses[0].locality

            btnmap_new.text = "$locality, $state($city), $pinCode $country"
            txtCity_new.text = city
            txtState_new.text = state
            txtPincode_new.text = pinCode
            txtLocality_new.text = locality
            txtCountry_new.text = country
            City_new.text = "($city),"
            State_new.text = state
            Pincode_new.text = " $pinCode"

            Locality_bold_new.text = locality
            localit_new.text = "$locality,"


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
            btnmap_new.text = "$locality, $state($city), $pinCode $country"
            txtCity_new.text = city
            txtState_new.text = state
            txtPincode_new.text = pinCode
            txtLocality_new.text = locality
            txtCountry_new.text = country
            City_new.text = "($city),"
            State_new.text = state
            Pincode_new.text = " $pinCode"

            Locality_bold_new.text = locality
            localit_new.text = "$locality,"


        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val intent=Intent(this,ManageAddress::class.java)
        startActivity(intent)
        finish()
    }
}