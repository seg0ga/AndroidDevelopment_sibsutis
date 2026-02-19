package com.example.android_development

import android.os.Bundle
import android.widget.Button
import android.net.Uri
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets}

        val bttn_calculator=findViewById<Button>(R.id.bttn_сalculator)
        val bttn_mediaplayer=findViewById<Button>(R.id.bttn_mediaplayer)
        val bttn_location=findViewById<Button>(R.id.bttn_location)
        val bttn_telephony=findViewById<Button>(R.id.bttn_telephony)
        val bttn_sockets=findViewById<Button>(R.id.bttn_sockets)
        val bttn_tasks=findViewById<Button>(R.id.bttn_tasks)

        bttn_calculator.setOnClickListener({
            val calculatorIntent = Intent(this, calc::class.java)
            startActivity(calculatorIntent)})

        bttn_mediaplayer.setOnClickListener({
            val meadiplayerIntent=Intent(this, mediaplayer::class.java)
            startActivity(meadiplayerIntent)})

        bttn_location.setOnClickListener({
            val locationIntent=Intent(this, location::class.java)
            startActivity(locationIntent)})

        bttn_telephony.setOnClickListener({
            val telephonyIntent=Intent(this, telephony::class.java)
            startActivity(telephonyIntent)})

        bttn_sockets.setOnClickListener({
            val socketsIntent=Intent(this, sockets::class.java)
            startActivity(socketsIntent)})



        bttn_tasks.setOnClickListener({
            val uri = Uri.parse("https://telecomdep.github.io/notes/HomeWork/home_work.html#")
            val tasksIntent=Intent(Intent.ACTION_VIEW,uri)
            startActivity(tasksIntent)
        })
    }
}