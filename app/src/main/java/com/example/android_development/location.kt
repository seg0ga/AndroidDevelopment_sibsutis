package com.example.android_development

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.io.File
import java.util.Date
import kotlin.math.round




class location : AppCompatActivity() {
    var file="location.json"
    lateinit var myFusedLocationProviderClient: FusedLocationProviderClient
    lateinit var lat: TextView
    lateinit var lon: TextView
    lateinit var alt: TextView
    lateinit var card_button: Button
    lateinit var time: TextView
    lateinit var test: TextView
    var first: Boolean=true
    var spisokLocation=mutableListOf<String>()
    lateinit var spisokloc_activity: ListView
    var handler= Handler()
    var lat_site: Double= 0.0
    var lon_site: Double=0.0
//    var testik: Int = 0

    companion object {private const val PERMISSION_REQUEST_ACCESS_LOCATION= 100}

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets}
        myFusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        lat=findViewById<TextView>(R.id.latitude)
        lon=findViewById<TextView>(R.id.longitude)
        alt=findViewById<TextView>(R.id.altitude)
        spisokloc_activity=findViewById<ListView>(R.id.listloc)
        time=findViewById<TextView>(R.id.current_time)
        card_button=findViewById<Button>(R.id.card_button)
//        test=findViewById<TextView>(R.id.test)
        getCurrentLocation()
        updatelocation()
        card_button.setOnClickListener({
            val uri=Uri.parse("https://geotree.ru/coordinates?lat=$lat_site&lon=$lon_site&z=15&mlat=$lat_site&mlon=$lon_site&c=")
            val siteIntent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(siteIntent)})}

    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) }

    private fun updatelocation(){
        handler.postDelayed({getCurrentLocation()
//            test.setText(testik.toString())
//            testik+=1
        updatelocation()}, 5000)}


    private fun updateSpisok(location_for_spisok: String){
        var adapter= ArrayAdapter(this,android.R.layout.simple_list_item_1,spisokLocation)
        spisokloc_activity.adapter=adapter
        spisokLocation.add(location_for_spisok)
    }


    private fun getCurrentLocation(){

        if(checkPermissions()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions()
                    return}
                myFusedLocationProviderClient.lastLocation.addOnCompleteListener(this){task->
                    val location:Location?=task.result
                    if(location==null){Toast.makeText(applicationContext, "problems with signal", Toast.LENGTH_SHORT).show()
                    } else {
                        lat_site= location.latitude
                        lon_site= location.longitude

                        lat.setText(location.latitude.toString())
                        lon.setText(location.longitude.toString())
                        alt.setText((round(location.altitude)).toString())

                        var hour=Date(location.time).hours.toString()
                        var minute=Date(location.time).minutes.toString()
                        var seconds=Date(location.time).seconds.toString()
                        var day=Date(location.time).day
                        var mount= Date(location.time).month
                        var year= Date(location.time).year

                        if (hour.length!=2){hour="0"+hour}
                        if (minute.length!=2){minute="0"+minute}
                        if (seconds.length!=2){seconds="0"+seconds}


                        var timeloc = hour+":"+minute+":"+seconds
                        var location_for_spisok = "Время: ${timeloc}\nШир: ${location.latitude} Дол: ${location.longitude}"
                        if (first){updateSpisok(location_for_spisok);first=false}

                        if (spisokLocation.last()!=location_for_spisok){
                            updateSpisok(location_for_spisok)}

                        time.setText(timeloc)}

                        val file=File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"location.json")
                        val json=JSONObject().apply {
                            put("lat",lat_site)
                            put("lon",lon_site)
                            put("time",Date().toString())}
                        file.writeText(json.toString())}
            }else{
                Toast.makeText(applicationContext,"Enable location in settings",Toast.LENGTH_SHORT).show()
                val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)}
        }else{
            lat.setText("Permission is not granted")
            lon.setText("Permission is not granted")
            alt.setText("Permission is not granted")
            requestPermissions()}}

    private fun requestPermissions(){ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_LOCATION)}

    private fun checkPermissions(): Boolean{
        if( ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED )
        {return true}
        else{return false}}


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) { super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_ACCESS_LOCATION)
        {if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_SHORT).show()
            getCurrentLocation()
            }else{Toast.makeText(applicationContext, "Denied by user", Toast.LENGTH_SHORT).show() }}}}