package com.example.android_development

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.telephony.*
import android.telephony.TelephonyManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat



class telephony : AppCompatActivity() {
    lateinit var bttn_lte: Button
    lateinit var bttn_gsm: Button
    lateinit var bttn_nr: Button
    lateinit var list_cell_info1: ListView
    lateinit var list_cell_info2: ListView
    lateinit var CellIdentity: TextView
    lateinit var CellSignalStrength: TextView
    var handler= Handler()
    var flag: Int=-1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_telephony)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets}
        bttn_lte=findViewById<Button>(R.id.bttn_lte)
        bttn_gsm=findViewById<Button>(R.id.bttn_gsm)
        bttn_nr=findViewById<Button>(R.id.bttn_nr)
        CellIdentity=findViewById<TextView>(R.id.CellIdentity)
        CellSignalStrength=findViewById<TextView>(R.id.CellSignalStrength)
        list_cell_info1=findViewById<ListView>(R.id.list_cell_info1)
        list_cell_info2=findViewById<ListView>(R.id.list_cell_info2)
        checkPermissions()

        bttn_lte.setOnClickListener({
            flag=1
            CellIdentity.setText("CellIdentityLTE")
            CellSignalStrength.setText("CellSignalStrengthLTE")
            bttn_lte.setBackgroundColor(Color.parseColor("#2d2d2f"))
            bttn_gsm.setBackgroundColor(Color.parseColor("#16c603"))
            bttn_nr.setBackgroundColor(Color.parseColor("#16c603"))})
        bttn_gsm.setOnClickListener({
            flag=2
            CellIdentity.setText("CellIdentityGSM")
            CellSignalStrength.setText("CellSignalStrengthGSM")
            bttn_lte.setBackgroundColor(Color.parseColor("#16c603"))
            bttn_gsm.setBackgroundColor(Color.parseColor("#2d2d2f"))
            bttn_nr.setBackgroundColor(Color.parseColor("#16c603"))})
        bttn_nr.setOnClickListener({
            flag=3
            CellIdentity.setText("CellIdentityNR")
            CellSignalStrength.setText("CellSignalStrengthNR")
            bttn_lte.setBackgroundColor(Color.parseColor("#16c603"))
            bttn_gsm.setBackgroundColor(Color.parseColor("#16c603"))
            bttn_nr.setBackgroundColor(Color.parseColor("#2d2d2f"))})
        updateCellInfo()}

    fun checkPermissions(){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED||
            ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
        ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.READ_PHONE_STATE,android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),1)}}


    private fun updateCellInfo(){
        handler.postDelayed({getCellInfo()
            handler.postDelayed({updateCellInfo()},5000)},5000)}

    fun getCellInfo(){
        if (flag==1){
            val cellIdentityList=mutableListOf<String>()
            val cellSignalStrengthList=mutableListOf<String>()
            val telephonyManager=getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val cellInfoList=telephonyManager.allCellInfo
            cellInfoList?.forEach{cellInfo->
                if (cellInfo is CellInfoLte){
                    cellIdentityList.add("Band:\n${cellInfo.cellIdentity.bandwidth}")
                    cellIdentityList.add("CellIdentity:\n${cellInfo.cellIdentity.ci}")
                    cellIdentityList.add("EARFCN:\n${cellInfo.cellIdentity.earfcn}")
                    cellIdentityList.add("MCC:\n${cellInfo.cellIdentity.mcc}")
                    cellIdentityList.add("MNC:\n${cellInfo.cellIdentity.mnc}")
                    cellIdentityList.add("PCI:\n${cellInfo.cellIdentity.pci}")
                    cellIdentityList.add("TAC:\n${cellInfo.cellIdentity.tac}")

                    cellSignalStrengthList.add("ASU Level:\n${cellInfo.cellSignalStrength.asuLevel}")
                    cellSignalStrengthList.add("CQI:\n${cellInfo.cellSignalStrength.cqi}")
                    cellSignalStrengthList.add("RSRP:\n${cellInfo.cellSignalStrength.rsrp}")
                    cellSignalStrengthList.add("RSRQ:\n${cellInfo.cellSignalStrength.rsrq}")
                    cellSignalStrengthList.add("RSSI:\n${cellInfo.cellSignalStrength.rssi}")
                    cellSignalStrengthList.add("RSSNR:\n${cellInfo.cellSignalStrength.rssnr}")
                    cellSignalStrengthList.add("Timing Advance:\n${cellInfo.cellSignalStrength.timingAdvance}")}}

            val a1= ArrayAdapter(this,android.R.layout.simple_list_item_1,cellIdentityList)
            list_cell_info1.adapter=a1
            val a2= ArrayAdapter(this,android.R.layout.simple_list_item_1,cellSignalStrengthList)
            list_cell_info2.adapter=a2}

        if (flag==2){
            val cellIdentityList=mutableListOf<String>()
            val cellSignalStrengthList=mutableListOf<String>()
            val telephonyManager=getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val cellInfoList=telephonyManager.allCellInfo
            cellInfoList?.forEach{cellInfo->
                if (cellInfo is CellInfoGsm){
                    cellIdentityList.add("CellIdentity:\n${cellInfo.cellIdentity.cid}")
                    cellIdentityList.add("BSIC:\n${cellInfo.cellIdentity.bsic}")
                    cellIdentityList.add("ARFCN:\n${cellInfo.cellIdentity.arfcn}")
                    cellIdentityList.add("LAC:\n${cellInfo.cellIdentity.lac}")
                    cellIdentityList.add("MCC:\n${cellInfo.cellIdentity.mcc}")
                    cellIdentityList.add("MNC:\n${cellInfo.cellIdentity.mnc}")
                    cellIdentityList.add("PSC:\n${cellInfo.cellIdentity.psc}")

                    cellSignalStrengthList.add("Dbm:\n${cellInfo.cellSignalStrength.dbm}")
                    cellSignalStrengthList.add("RSSI:\n${cellInfo.cellSignalStrength.rssi}")
                    cellSignalStrengthList.add("Timing Advance:\n${cellInfo.cellSignalStrength.timingAdvance}")}}

            val a1= ArrayAdapter(this,android.R.layout.simple_list_item_1,cellIdentityList)
            list_cell_info1.adapter=a1
            val a2= ArrayAdapter(this,android.R.layout.simple_list_item_1,cellSignalStrengthList)
            list_cell_info2.adapter=a2}

        if (flag==3){
            val cellIdentityList=mutableListOf<String>()
            val cellSignalStrengthList=mutableListOf<String>()
            val telephonyManager=getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val cellInfoList=telephonyManager.allCellInfo
            cellInfoList?.forEach{cellInfo->
                if (cellInfo is CellInfoNr){
                    val cellIdentity=cellInfo.cellIdentity as android.telephony.CellIdentityNr
                    val cellSignalStrength=cellInfo.cellSignalStrength as android.telephony.CellSignalStrengthNr
                    cellIdentityList.add("Band:\n${cellIdentity.bands}")
                    cellIdentityList.add("NCI:\n${cellIdentity.nci}")
                    cellIdentityList.add("PCI:\n${cellIdentity.pci}")
                    cellIdentityList.add("Nrargcn:\n${cellIdentity.nrarfcn}")
                    cellIdentityList.add("TAC:\n${cellIdentity.tac}")
                    cellIdentityList.add("MCC:\n${cellIdentity.mccString}")
                    cellIdentityList.add("MNC:\n${cellIdentity.mncString}")

                    cellSignalStrengthList.add("SS-RSRP:\n${cellSignalStrength.dbm}")
                    cellSignalStrengthList.add("SS-RSRQ:\n${cellSignalStrength.ssRsrq}")
                    cellSignalStrengthList.add("SS-SINR:\n${cellSignalStrength.ssSinr}")
                    cellSignalStrengthList.add("Timing Advance:\n${cellSignalStrength.timingAdvanceMicros}")}

            val a1= ArrayAdapter(this,android.R.layout.simple_list_item_1,cellIdentityList)
            list_cell_info1.adapter=a1
            val a2= ArrayAdapter(this,android.R.layout.simple_list_item_1,cellSignalStrengthList)
            list_cell_info2.adapter=a2}}}}