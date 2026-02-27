package com.example.android_development

import android.Manifest
import android.content.Context
import android.content.Intent
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

class background_service:AppCompatActivity() {
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
    lateinit var topAppsList:ListView
    lateinit var myFusedLocationProviderClient:FusedLocationProviderClient
    var flag:Int=1
    var handler=Handler()
    var lat_site:Double=0.0
    var lon_site:Double=0.0

    companion object {private const val PERMISSION_REQUEST_CODE=100}

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
        topAppsList=findViewById(R.id.top_apps_list)

        checkPermissions()

        bttnLte.setOnClickListener{
            flag=1
            updateButtonColors(bttnLte)
            cellIdentityTitle.text="CellIdentityLTE"
            cellSignalTitle.text="CellSignalStrengthLTE"
            getCellInfo()}

        bttnGsm.setOnClickListener{
            flag=2
            updateButtonColors(bttnGsm)
            cellIdentityTitle.text="CellIdentityGSM"
            cellSignalTitle.text="CellSignalStrengthGSM"
            getCellInfo()}

        bttnNr.setOnClickListener{
            flag=3
            updateButtonColors(bttnNr)
            cellIdentityTitle.text="CellIdentityNR"
            cellSignalTitle.text="CellSignalStrengthNR"
            getCellInfo()}

        updateLocation()
        updateCellInfo()}

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

    private fun checkLocationPermissions():Boolean{
        return ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED}

    private fun checkPhonePermission():Boolean{
        return ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED}

    private fun isLocationEnabled():Boolean{
        val locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)}

    private fun updateLocation(){
        handler.postDelayed({
            getCurrentLocation()
            updateLocation()},5000)}

    private fun updateCellInfo(){
        handler.postDelayed({
            getCellInfo()
            updateCellInfo()},5000)}

    private fun getCurrentLocation(){
        if (checkLocationPermissions()){
            if (isLocationEnabled()){
                myFusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener(this){task->
                        val location:Location?=task.result
                        if (location!=null){
                            lat_site=location.latitude
                            lon_site=location.longitude
                            latitudeText.text=location.latitude.toString()
                            longitudeText.text=location.longitude.toString()
                            altitudeText.text=round(location.altitude).toString()
                            accuracyText.text=location.accuracy.toString()
                            var hour=Date(location.time).hours.toString()
                            var minute=Date(location.time).minutes.toString()
                            var seconds=Date(location.time).seconds.toString()
                            if (hour.length!=2)hour="0$hour"
                            if (minute.length!=2)minute="0$minute"
                            if (seconds.length!=2)seconds="0$seconds"
                            timeText.text="$hour:$minute:$seconds"
                            val totalTraffic=TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes()
                            trafficTotalText.text="${totalTraffic/1024/1024} MB"
                            sendAllData(location)}}
            }else{Toast.makeText(applicationContext,"Enable location",Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))}}}

    private fun getCellInfo(){
        if (!checkPhonePermission()){return}

        val telephonyManager=getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val cellInfoList=telephonyManager.allCellInfo
        val identityList=mutableListOf<String>()
        val strengthList=mutableListOf<String>()

        if (cellInfoList!=null){
            for (cellInfo in cellInfoList){
                when {
                    cellInfo is CellInfoLte&&flag==1->{
                        identityList.add("Band: ${cellInfo.cellIdentity.bandwidth}")
                        identityList.add("CI: ${cellInfo.cellIdentity.ci}")
                        identityList.add("EARFCN: ${cellInfo.cellIdentity.earfcn}")
                        identityList.add("MCC: ${cellInfo.cellIdentity.mcc}")
                        identityList.add("MNC: ${cellInfo.cellIdentity.mnc}")
                        identityList.add("PCI: ${cellInfo.cellIdentity.pci}")
                        identityList.add("TAC: ${cellInfo.cellIdentity.tac}")
                        strengthList.add("ASU: ${cellInfo.cellSignalStrength.asuLevel}")
                        strengthList.add("CQI: ${cellInfo.cellSignalStrength.cqi}")
                        strengthList.add("RSRP: ${cellInfo.cellSignalStrength.rsrp}")
                        strengthList.add("RSRQ: ${cellInfo.cellSignalStrength.rsrq}")
                        strengthList.add("RSSI: ${cellInfo.cellSignalStrength.rssi}")
                        strengthList.add("RSSNR: ${cellInfo.cellSignalStrength.rssnr}")
                        strengthList.add("TA: ${cellInfo.cellSignalStrength.timingAdvance}")}
                    cellInfo is CellInfoGsm&&flag==2->{
                        identityList.add("CID: ${cellInfo.cellIdentity.cid}")
                        identityList.add("BSIC: ${cellInfo.cellIdentity.bsic}")
                        identityList.add("ARFCN: ${cellInfo.cellIdentity.arfcn}")
                        identityList.add("LAC: ${cellInfo.cellIdentity.lac}")
                        identityList.add("MCC: ${cellInfo.cellIdentity.mcc}")
                        identityList.add("MNC: ${cellInfo.cellIdentity.mnc}")
                        strengthList.add("Dbm: ${cellInfo.cellSignalStrength.dbm}")
                        strengthList.add("RSSI: ${cellInfo.cellSignalStrength.rssi}")
                        strengthList.add("TA: ${cellInfo.cellSignalStrength.timingAdvance}")}
                    cellInfo is CellInfoNr&&flag==3->{
                        val cellIdentity=cellInfo.cellIdentity as CellIdentityNr
                        val cellSignal=cellInfo.cellSignalStrength as CellSignalStrengthNr
                        identityList.add("Band: ${cellIdentity.bands}")
                        identityList.add("NCI: ${cellIdentity.nci}")
                        identityList.add("PCI: ${cellIdentity.pci}")
                        identityList.add("NRARFCN: ${cellIdentity.nrarfcn}")
                        identityList.add("TAC: ${cellIdentity.tac}")
                        identityList.add("MCC: ${cellIdentity.mccString}")
                        identityList.add("MNC: ${cellIdentity.mncString}")
                        strengthList.add("RSRP: ${cellSignal.dbm}")
                        strengthList.add("RSRQ: ${cellSignal.ssRsrq}")
                        strengthList.add("SINR: ${cellSignal.ssSinr}")
                        strengthList.add("TA: ${cellSignal.timingAdvanceMicros}")}}}}

        listCellInfo1.adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,identityList)
        listCellInfo2.adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,strengthList)}

    private fun sendAllData(location:Location){
        try {val jsonData=JSONObject()

            val locData=JSONObject()
            locData.put("latitude",location.latitude)
            locData.put("longitude",location.longitude)
            locData.put("altitude",round(location.altitude))
            locData.put("accuracy",location.accuracy)
            locData.put("current_time",location.time.toString())
            jsonData.put("location",locData)
            if (checkPhonePermission()){
                val telephonyManager=getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val cellInfoList=telephonyManager.allCellInfo
                val cellsArray=JSONArray()

                if (cellInfoList!=null){
                    for (cellInfo in cellInfoList){
                        val cellData=JSONObject()
                        when (cellInfo){
                            is CellInfoLte->{
                                cellData.put("type","LTE")
                                cellData.put("band",cellInfo.cellIdentity.bandwidth)
                                cellData.put("cell_identity",cellInfo.cellIdentity.ci)
                                cellData.put("earfcn",cellInfo.cellIdentity.earfcn)
                                cellData.put("mcc",cellInfo.cellIdentity.mcc)
                                cellData.put("mnc",cellInfo.cellIdentity.mnc)
                                cellData.put("pci",cellInfo.cellIdentity.pci)
                                cellData.put("tac",cellInfo.cellIdentity.tac)
                                cellData.put("asu_level",cellInfo.cellSignalStrength.asuLevel)
                                cellData.put("cqi",cellInfo.cellSignalStrength.cqi)
                                cellData.put("rsrp",cellInfo.cellSignalStrength.rsrp)
                                cellData.put("rsrq",cellInfo.cellSignalStrength.rsrq)
                                cellData.put("rssi",cellInfo.cellSignalStrength.rssi)
                                cellData.put("rssnr",cellInfo.cellSignalStrength.rssnr)
                                cellData.put("timing_advance",cellInfo.cellSignalStrength.timingAdvance)}

                            is CellInfoGsm->{
                                cellData.put("type","GSM")
                                cellData.put("cell_identity",cellInfo.cellIdentity.cid)
                                cellData.put("bsic",cellInfo.cellIdentity.bsic)
                                cellData.put("arfcn",cellInfo.cellIdentity.arfcn)
                                cellData.put("lac",cellInfo.cellIdentity.lac)
                                cellData.put("mcc",cellInfo.cellIdentity.mcc)
                                cellData.put("mnc",cellInfo.cellIdentity.mnc)
                                cellData.put("psc",cellInfo.cellIdentity.psc)
                                cellData.put("dbm",cellInfo.cellSignalStrength.dbm)
                                cellData.put("rssi",cellInfo.cellSignalStrength.rssi)
                                cellData.put("timing_advance",cellInfo.cellSignalStrength.timingAdvance)}

                            is CellInfoNr->{
                                cellData.put("type","NR")
                                val cellIdentity=cellInfo.cellIdentity as CellIdentityNr
                                val cellSignal=cellInfo.cellSignalStrength as CellSignalStrengthNr
                                cellData.put("band",cellIdentity.bands?.joinToString(","))
                                cellData.put("nci",cellIdentity.nci)
                                cellData.put("pci",cellIdentity.pci)
                                cellData.put("nrarfcn",cellIdentity.nrarfcn)
                                cellData.put("tac",cellIdentity.tac)
                                cellData.put("mcc",cellIdentity.mccString)
                                cellData.put("mnc",cellIdentity.mncString)
                                cellData.put("ss_rsrp",cellSignal.dbm)
                                cellData.put("ss_rsrq",cellSignal.ssRsrq)
                                cellData.put("ss_sinr",cellSignal.ssSinr)
                                cellData.put("timing_advance",cellSignal.timingAdvanceMicros)}}
                        cellsArray.put(cellData)}}
                jsonData.put("cells",cellsArray)}

            val trafficData=JSONObject()
            trafficData.put("total_rx",TrafficStats.getTotalRxBytes())
            trafficData.put("total_tx",TrafficStats.getTotalTxBytes())
            trafficData.put("total",TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes())
            jsonData.put("traffic",trafficData)
            saveToFile(jsonData)
            Thread{
                try{
                    val context=ZMQ.context(1)
                    val socket=ZContext().createSocket(SocketType.REQ)
                    socket.connect("tcp://172.22.237.35:5555")
                    socket.send(jsonData.toString().toByteArray(ZMQ.CHARSET),0)
                    socket.close()
                    context.close()
                }catch(e:Exception){e.printStackTrace()}
            }.start()
        }catch (e:Exception){e.printStackTrace()}}

    private fun saveToFile(jsonData:JSONObject){
        try {
            val downloadsDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file=File(downloadsDir,"mydata.json")
            val jsonObject=if (file.exists()) JSONObject(file.readText()) else JSONObject()
            val dataArray=if (jsonObject.has("data")) jsonObject.getJSONArray("data") else JSONArray()
            dataArray.put(jsonData)
            jsonObject.put("data",dataArray)
            file.writeText(jsonObject.toString())
        }catch(e:Exception){e.printStackTrace()}}

    override fun onRequestPermissionsResult(requestCode:Int,permissions:Array<out String>,grantResults:IntArray){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if (requestCode==PERMISSION_REQUEST_CODE){
            if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permissions granted",Toast.LENGTH_SHORT).show()
                getCurrentLocation()}}}}