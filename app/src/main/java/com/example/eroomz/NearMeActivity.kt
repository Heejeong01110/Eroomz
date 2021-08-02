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
        information.setOnClickListener {
            if (isPageOpen) {
                informationPage.startAnimation(Downanim)
            }
            else{
                informationPage.visibility = View.VISIBLE
                informationPage.startAnimation(Upanim)


            }
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btn_navi.setOnClickListener{
            layout_drawer.openDrawer(GravityCompat.START)  //START = LEFT
        }

        naviView.setNavigationItemSelectedListener (this) //네비게이션 메뉴 아이템에 클릭 속성 부여

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
}

