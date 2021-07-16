package com.example.eroomz

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.near_me_activity.*


const val EXTRA_LAT = "com.example.eroomz.LAT"
const val EXTRA_LNG = "com.example.eroomz.LNG"

class NearMeActivity : AppCompatActivity(R.layout.activity_app_main), OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(item: MenuItem): Boolean { //네비게이션 메뉴 아이템 클릭시 수행
        when(item.itemId){
            R.id.facilities ->Toast.makeText(applicationContext,"편의시설",Toast.LENGTH_SHORT).show() // 네비게이션 버튼 클릭하면 나오는 창
            R.id.commuting_money ->Toast.makeText(applicationContext,"통근비용",Toast.LENGTH_SHORT).show()
            R.id.cost ->Toast.makeText(applicationContext,"월세/전세",Toast.LENGTH_SHORT).show()

        }
        layout_drawer.closeDrawers() //네비게이션 뷰 닫기
        return false
    }

    override fun onBackPressed() { //뒤로가기 누를때 수행하는 함수
        if(layout_drawer.isDrawerOpen(GravityCompat.START)){
            layout_drawer.closeDrawers()
        }
        else {
            super.onBackPressed()
        }
    }





    var PERMISSION_ID = 1000
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationManager : LocationManager? = null
    lateinit var locationRequest: LocationRequest

    private var lat = 0.0F
    private var lng = 0.0F

    var isPageOpen : Boolean = false //상세정보 누르냐 안누르냐
    lateinit var Upanim : Animation //아래에서 위로
    lateinit var Downanim : Animation //위에서 아래로

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.near_me_activity)

        Upanim = AnimationUtils.loadAnimation(this, R.anim.translate_up)
        Downanim = AnimationUtils.loadAnimation(this,R.anim.translate_down)

        Upanim.setAnimationListener(SlidingPageAnimationListener())
        Downanim.setAnimationListener(SlidingPageAnimationListener())

        //상세정보
        information.setOnClickListener(){
            if (isPageOpen) {
                informationPage.startAnimation(Downanim)

            }
            else{
                informationPage.visibility = View.VISIBLE
                informationPage.startAnimation(Upanim)


            }
        }


        btn_navi.setOnClickListener{
            layout_drawer.openDrawer(GravityCompat.START)  //START = LEFT
        }

        naviView.setNavigationItemSelectedListener (this) //네비게이션 메뉴 아이템에 클릭 속성 부여

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()


    }
    //상세정보
    private inner class SlidingPageAnimationListener : Animation.AnimationListener {

        override fun onAnimationEnd(animation: Animation?) : Unit {
            if(isPageOpen){
                informationPage.visibility= View.INVISIBLE

                information.text="Open"
                isPageOpen = false
            }
            else{
                information.text="Close"
                isPageOpen = true
            }
        }

        override fun onAnimationStart(animation: Animation?) {

        }

        override fun onAnimationRepeat(animation: Animation?) {
        }
    }
//상세정보 끝

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(lat.toDouble(), lng.toDouble()))
                .title(lat.toString())
        )
    }


    private fun getLastLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){
                fusedLocationProviderClient.lastLocation.addOnCompleteListener{ task ->
                    var location: Location? = task.result
                    Log.i("Location Log", location.toString())
                    if(location == null){
                        Log.i("TAG", "null called")
                        getNewLocation()
                    }else{
                        Log.i("TAG", location.latitude.toString())

                        lat = location.latitude.toFloat()
                        lng = location.longitude.toFloat()
                        latText.text = lat.toString()
                        lngText.text = lng.toString()
                        val mapFragment = supportFragmentManager
                            .findFragmentById(R.id.map) as SupportMapFragment
                        mapFragment.getMapAsync(this)
                    }
                }
            }else{
                Toast.makeText(this,"Please Enable your location service", Toast.LENGTH_SHORT).show()
            }
        }else{
            requestPermission()
        }
        Log.i("TAG", lat.toString())
        Log.i("TAG", lng.toString())

    }


    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult?) {
            var lastLocation = p0?.lastLocation
            lat = lastLocation?.latitude?.toFloat()!!
            lng = lastLocation?.longitude?.toFloat()
        }
    }

    private fun checkPermission(): Boolean{
        if(
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) { return true }

        return false
    }

    @SuppressLint("MissingPermission")
    private fun getNewLocation(){
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 2

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,locationCallback, Looper.myLooper()
        )

    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION),PERMISSION_ID)
    }

    private fun isLocationEnabled(): Boolean{
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i("Debug:", "You have location Permission")
                Log.d("Debug:", "You have location Permission")
            }
        }
    }


}

