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
import com.example.localzes.Modals.ModelClass
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_maps.*
import util.ConnectionManager
import java.io.IOException
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
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
    private lateinit var suserDatabase: DatabaseReference
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
                    if (e is ResolvableApiException) {
                        try {
                            e.startResolutionForResult(this@MapsActivity, 51)
                        } catch (e1: IntentSender.SendIntentException) {
                            e1.printStackTrace()
                        }
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
                                e.startResolutionForResult(this@MapsActivity, 51)
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
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        retryMapC.setOnClickListener {
            if(ConnectionManager().checkConnectivity(this)){
                rl_mapC.visibility= View.VISIBLE
                rl_retryMapC.visibility=View.GONE
                this.recreate()
            }else{
                rl_mapC.visibility= View.GONE
                rl_retryMapC.visibility=View.VISIBLE
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user!!.uid
        suserDatabase = FirebaseDatabase.getInstance().reference.child("customers").child(uid)
        SaveAddress.setOnClickListener {
            if (edtLocality.text.toString().isEmpty()) {
                edtLocality.error = "Please Enter Your Locality"
                return@setOnClickListener
            }
            else if (edtNearestLandmark.text.toString().isEmpty()) {
                edtNearestLandmark.error = "Please Enter Your nearest Landmark"
                return@setOnClickListener
            } else {

            if(ConnectionManager().checkConnectivity(this)) {
                rl_mapC.visibility= View.VISIBLE
                rl_retryMapC.visibility=View.GONE
                suserDatabase!!.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {

                            val user: ModelClass? = snapshot.getValue(
                                ModelClass::class.java
                            )
                            val phone: String? = user!!.getPhone()
                            val name = intent.getStringExtra("name")
                            val email = intent.getStringExtra("email")
                            val address = btnmap.text.toString()
                            customReg(name, email, phone, address)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })}else{
                rl_mapC.visibility= View.GONE
                rl_retryMapC.visibility=View.VISIBLE
            }
            }
        }
    }

    private fun customReg(name: String?, email: String?, phone: String?, address: String) {
        val user = auth.currentUser
        var uid = user!!.uid
        userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(uid)

        val userMap = HashMap<String, Any>()
        userMap["uid"] = uid
        userMap["phone"] = phone.toString()
        userMap["name"] = name.toString()
        userMap["email"] = email.toString()
        userMap["address"] = address
        userDatabase.setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val city = txtCity.text.toString()
                val state = txtState.text.toString()
                val country = txtCountry.text.toString()
                val pinCode = txtPincode.text.toString()
                val locality = txtLocality.text.toString()
                val locality2 = edtLocality.text.toString()
                val houseNo = HouseNo.text.toString()
                val nearestLandmark = edtNearestLandmark.text.toString()
                userDatabase = FirebaseDatabase.getInstance().reference.child("users").child(uid)
                    .child("current_address")
                val userMaps = HashMap<String, Any>()
                userMaps["address"] = address
                userMaps["city"] = city
                userMaps["state"] = state
                userMaps["country"] = country
                userMaps["pinCode"] = pinCode
                userMaps["locality"] = locality
                userMaps["locality2"] = locality2
                userMaps["houseBlock"] = houseNo
                userMaps["nearestLandmark"] = nearestLandmark
                userMaps["mobileNo"] = phone.toString()
                userDatabase.setValue(userMaps).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        startActivity(Intent(this, Home::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            baseContext, "Failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                Toast.makeText(
                    baseContext, "Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        // ...

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

                    try {
                        geocoder = Geocoder(applicationContext, Locale.getDefault())
                        addresses = geocoder.getFromLocation(lot, long, 1)
                        val address: String = addresses[0].getAddressLine(0)
                        val city: String = addresses[0].subAdminArea
                        val state: String = addresses[0].adminArea
                        val pinCode: String = addresses[0].postalCode
                        val country: String = addresses[0].countryName

                        btnmap.text = address
                        txtCity.text = city
                        txtState.text = state
                        txtPincode.text = pinCode
                        txtCountry.text = country
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    fusedLocationClient.removeLocationUpdates(locationCallback)



                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        //  val markerOptions = MarkerOptions().position(latLng)
                        //  map.addMarker(markerOptions)
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

            btnmap.text = "$locality, $state($city), $pinCode $country"
            txtCity.text = city
            txtState.text = state
            txtPincode.text = pinCode
            txtLocality.text = locality
            txtCountry.text = country
            City.text = "($city),"
            State.text = state
            Pincode.text = " $pinCode"

            Locality_bold.text = locality
            localit.text = "$locality,"
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

            btnmap.text = "$locality, $state($city), $pinCode $country"
            txtCity.text = city
            txtState.text = state
            txtPincode.text = pinCode
            txtLocality.text = locality
            txtCountry.text = country
            City.text = "($city),"
            State.text = state
            Pincode.text = " $pinCode"

            Locality_bold.text = locality
            localit.text = "$locality,"

        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, Registerdetails::class.java)
        startActivity(intent)
        finish()
    }
}
