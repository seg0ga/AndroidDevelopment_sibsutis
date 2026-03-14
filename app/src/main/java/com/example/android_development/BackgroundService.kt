package com.example.android_development

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import java.io.File
import java.util.Date
import kotlin.math.round

class BackgroundService:Service(){
    private val serviceJob=Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO+serviceJob)
    lateinit var myFusedLocationProviderClient:FusedLocationProviderClient
    var flag:Int=1
    var lat_site:Double=0.0
    var lon_site:Double=0.0

    override fun onCreate(){
        super.onCreate()
        myFusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)}

    override fun onStartCommand(intent:Intent?,flags:Int,startId:Int):Int{
        flag=intent?.getIntExtra("flag",1)?:1
        serviceScope.launch{
            while (isActive){
                getCurrentLocation()
                getCellInfo()
                delay(3000)}}
        return START_STICKY}

    private fun sendLocationToActivity(location:Location){
        val intent=Intent("LOCATION_UPDATE")
        intent.putExtra("latitude",location.latitude.toString())
        intent.putExtra("longitude",location.longitude.toString())
        intent.putExtra("altitude",round(location.altitude).toString())
        intent.putExtra("accuracy",location.accuracy.toString())
        var hour=Date(location.time).hours.toString()
        var minute=Date(location.time).minutes.toString()
        var seconds=Date(location.time).seconds.toString()
        if (hour.length!=2) hour="0$hour"
        if (minute.length!=2) minute="0$minute"
        if (seconds.length!=2) seconds="0$seconds"
        intent.putExtra("time","$hour:$minute:$seconds")

        val totalTraffic=TrafficStats.getTotalRxBytes()+TrafficStats.getTotalTxBytes()
        intent.putExtra("traffic","${totalTraffic/1024/1024} MB")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)}

    private fun sendCellInfoToActivity(identityList:MutableList<String>,strengthList:MutableList<String>){
        val intent=Intent("CELL_INFO_UPDATE")
        intent.putStringArrayListExtra("identity_list",ArrayList(identityList))
        intent.putStringArrayListExtra("strength_list",ArrayList(strengthList))
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)}

    override fun onBind(intent:Intent):IBinder{TODO("Return the communication channel to the service.")}

    private fun getCurrentLocation(){
        if (checkLocationPermissions()){
            if (isLocationEnabled()){
                myFusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener{task->
                    val location:Location?=task.result
                    if (location!=null){
                        lat_site=location.latitude
                        lon_site=location.longitude
                        sendLocationToActivity(location)
                        sendAllData(location)}}}}}

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
        sendCellInfoToActivity(identityList,strengthList)}

    private fun sendAllData(location:Location){
        try {val jsonData=JSONObject()

            val locData=JSONObject()
            locData.put("latitude",location.latitude)
            locData.put("longitude",location.longitude)
            locData.put("altitude",round(location.altitude))
            locData.put("accuracy",location.accuracy)
            locData.put("current_time",location.time)
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
                    socket.connect("tcp://5.128.213.219:443")
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

    private fun checkLocationPermissions():Boolean{
        return ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED}

    private fun checkPhonePermission():Boolean{
        return ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED}

    private fun isLocationEnabled():Boolean{
        val locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)}

    override fun onDestroy(){
        super.onDestroy()
        serviceJob.cancel()}}