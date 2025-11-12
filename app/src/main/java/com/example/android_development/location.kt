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
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Date


class location : AppCompatActivity() {

    lateinit var myFusedLocationProviderClient: FusedLocationProviderClient
    lateinit var lat: TextView
    lateinit var lon: TextView
    lateinit var alt: TextView
    lateinit var card_button: Button
    lateinit var time: TextView
    var lat_site: Double= 0.0
    var lon_site: Double=0.0

    companion object {private const val PERMISSION_REQUEST_ACCESS_LOCATION= 100}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_location)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets }
        myFusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        lat=findViewById<TextView>(R.id.latitude)
        lon=findViewById<TextView>(R.id.longitude)
        alt=findViewById<TextView>(R.id.altitude)
        time=findViewById<TextView>(R.id.current_time)
        card_button=findViewById<Button>(R.id.card_button)
        getCurrentLocation()
        card_button.setOnClickListener({
            val uri=Uri.parse("https://geotree.ru/coordinates?lat=$lat_site&lon=$lon_site&z=15&mlat=$lat_site&mlon=$lon_site&c=")
            val siteIntent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(siteIntent)})}

    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) }

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
                        alt.setText(location.altitude.toString())
                        val hour=Date(location.time).hours.toString()
                        val minute=Date(location.time).minutes.toString()
                        val seconds=Date(location.time).seconds.toString()
                        val day=Date(location.time).day
                        val mount= Date(location.time).month
                        val year= Date(location.time).year
                        time.setText(hour+":"+minute+":"+seconds)}}

            } else{
                Toast.makeText(applicationContext, "Enable location in settings", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent) }
        } else {
            lat.setText("Permission is not granted")
            lon.setText("Permission is not granted")
            alt.setText("Permission is not granted")
            requestPermissions()}}

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )}

    private fun checkPermissions(): Boolean{
        if( ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
        {return true
        }else{return false}}


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