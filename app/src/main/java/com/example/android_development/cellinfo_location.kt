package com.example.android_development

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.net.TrafficStats
import android.os.*
import android.provider.Settings
import android.telephony.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.json.JSONArray
import org.json.JSONObject
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import java.io.File
import java.util.Date
import kotlin.math.round

class cellinfo_location:AppCompatActivity() {
    lateinit var latitudeText:TextView
    lateinit var longitudeText:TextView
    lateinit var altitudeText:TextView
    lateinit var accuracyText:TextView
    lateinit var timeText:TextView
    lateinit var trafficTotalText:TextView
    lateinit var bttnLte:Button
    lateinit var bttnGsm:Button
    lateinit var bttnNr:Button
    lateinit var listCellInfo1:ListView
    lateinit var listCellInfo2:ListView
    lateinit var cellIdentityTitle:TextView
    lateinit var cellSignalTitle:TextView
    lateinit var myFusedLocationProviderClient:FusedLocationProviderClient
    var flag:Int=1
    var handler=Handler()
    var lat_site:Double=0.0
    var lon_site:Double=0.0
    lateinit var bStart:Button
    lateinit var bStop:Button

    companion object {private const val PERMISSION_REQUEST_CODE=100}

    private val mMessageReceiver=object:BroadcastReceiver(){
        override fun onReceive(context:Context?,intent:Intent){
            when (intent.action){
                "LOCATION_UPDATE"->{
                    latitudeText.text=intent.getStringExtra("latitude")
                    longitudeText.text=intent.getStringExtra("longitude")
                    altitudeText.text=intent.getStringExtra("altitude")
                    accuracyText.text=intent.getStringExtra("accuracy")
                    timeText.text=intent.getStringExtra("time")
                    trafficTotalText.text=intent.getStringExtra("traffic")}
                "CELL_INFO_UPDATE"->{
                    val identityList=intent.getStringArrayListExtra("identity_list")
                    val strengthList=intent.getStringArrayListExtra("strength_list")
                    if (identityList!=null){listCellInfo1.adapter=ArrayAdapter(this@cellinfo_location, android.R.layout.simple_list_item_1, identityList)}
                    if (strengthList!=null){listCellInfo2.adapter = ArrayAdapter(this@cellinfo_location, android.R.layout.simple_list_item_1, strengthList)}}}}}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_service)

        myFusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        latitudeText=findViewById(R.id.latitude_service)
        longitudeText=findViewById(R.id.longitude_service)
        altitudeText=findViewById(R.id.altitude_service)
        accuracyText=findViewById(R.id.accuracy_service)
        timeText=findViewById(R.id.current_time_service)
        trafficTotalText=findViewById(R.id.traffic_total)
        bttnLte=findViewById(R.id.bttn_lte)
        bttnGsm=findViewById(R.id.bttn_gsm)
        bttnNr=findViewById(R.id.bttn_nr)
        cellIdentityTitle=findViewById(R.id.CellIdentity)
        cellSignalTitle=findViewById(R.id.CellSignalStrength)
        listCellInfo1=findViewById(R.id.list_cell_info1)
        listCellInfo2=findViewById(R.id.list_cell_info2)
        bStart=findViewById(R.id.bStartBg)
        bStop=findViewById(R.id.bStopBg)

        checkPermissions()

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,IntentFilter("LOCATION_UPDATE"))
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,IntentFilter("CELL_INFO_UPDATE"))

        bttnLte.setOnClickListener{
            flag=1
            updateButtonColors(bttnLte)
            cellIdentityTitle.text="CellIdentityLTE"
            cellSignalTitle.text="CellSignalStrengthLTE"}

        bttnGsm.setOnClickListener{
            flag=2
            updateButtonColors(bttnGsm)
            cellIdentityTitle.text="CellIdentityGSM"
            cellSignalTitle.text="CellSignalStrengthGSM"}

        bttnNr.setOnClickListener{
            flag=3
            updateButtonColors(bttnNr)
            cellIdentityTitle.text="CellIdentityNR"
            cellSignalTitle.text="CellSignalStrengthNR"}

        bStart.setOnClickListener{
            val startBgIntent=Intent(this,BackgroundService::class.java)
            startBgIntent.putExtra("flag",flag)
            startService(startBgIntent)}

        bStop.setOnClickListener{
            val stopBgIntent=Intent(this, BackgroundService::class.java)
            stopService(stopBgIntent)}}

    private fun updateButtonColors(selectedButton: Button){
        bttnLte.setBackgroundColor(if (selectedButton==bttnLte) Color.parseColor("#2d2d2f") else Color.parseColor("#16c603"))
        bttnGsm.setBackgroundColor(if (selectedButton==bttnGsm) Color.parseColor("#2d2d2f") else Color.parseColor("#16c603"))
        bttnNr.setBackgroundColor(if (selectedButton==bttnNr) Color.parseColor("#2d2d2f") else Color.parseColor("#16c603"))}

    private fun checkPermissions(){
        val permissions=arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE)

        val permissionsToRequest=mutableListOf<String>()
        for (permission in permissions){
            if (ActivityCompat.checkSelfPermission(this,permission)!=PackageManager.PERMISSION_GRANTED){permissionsToRequest.add(permission)}}
        if (permissionsToRequest.isNotEmpty()){ActivityCompat.requestPermissions(this,permissionsToRequest.toTypedArray(),PERMISSION_REQUEST_CODE)}}

    override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<out String>,grantResults:IntArray){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if (requestCode==PERMISSION_REQUEST_CODE){
            if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permissions granted",Toast.LENGTH_SHORT).show()}}}}