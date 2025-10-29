package com.example.android_development

import android.os.Bundle
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import android.widget.Toast
import android.os.Environment
import android.util.Log
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.content.Intent
import android.media.MediaPlayer


class mediaplayer : AppCompatActivity() {
    var play_p: Boolean=false
    lateinit var bttn_play: ImageButton
    lateinit var name: TextView
    lateinit var author: TextView
    lateinit var seekBar: SeekBar
    lateinit var audio: MutableList<File>
    lateinit var mediaplayer: MediaPlayer
    var count: Int = 0
    var log_tag : String = "MY_LOG_TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mediaplayer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets}
        
        bttn_play=findViewById<ImageButton>(R.id.bttn_play)
        var bttn_forward=findViewById<ImageButton>(R.id.bttn_forward)
        var bttn_back=findViewById<ImageButton>(R.id.bttn_back)
        var bttn_dir=findViewById<ImageButton>(R.id.bttn_dir)
        name=findViewById<TextView>(R.id.textView_name)
        author=findViewById<TextView>(R.id.textView_author)
        seekBar=findViewById<SeekBar>(R.id.seekBar)

        bttn_play.setOnClickListener{play()}
        bttn_forward.setOnClickListener{nextTrack()}
        bttn_back.setOnClickListener{predTrack()}
        bttn_dir.setOnClickListener{directory()}

        mediaplayer= MediaPlayer()
        audio=mutableListOf()

        val requestPermessionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                playMusic()
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Please grant permission", Toast.LENGTH_LONG).show()
            }}
        requestPermessionLauncher.launch(READ_MEDIA_AUDIO)}

    private fun playMusic(){
        var musicPath: String = Environment.getExternalStorageDirectory().path
        Log.d (log_tag, "PATH: " + musicPath)
        var directory: File = File(musicPath)

        audio.clear()
        directory.listFiles()?.forEach{ file ->
            if (file.name.endsWith(".mp3")) {
                audio.add(file)}}

        if (audio.isNotEmpty()){
            playSong(audio[0])}}


    fun playSong(file: File) {
        mediaplayer.reset()
        mediaplayer.setDataSource(file.absolutePath)
        mediaplayer.prepare()
        mediaplayer.start()
        name.text=file.nameWithoutExtension
        bttn_play.setImageResource(R.drawable.play)
        play_p=true
    }


    fun play(){
        if (play_p==false){
            playSong(audio[0])
            mediaplayer.start()
            bttn_play.setImageResource(R.drawable.play)
            play_p=true
            }
        else{
            mediaplayer.pause()
            bttn_play.setImageResource(R.drawable.pause)
            play_p=false}}



    fun nextTrack(){}

    fun predTrack(){}

    fun directory(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        startActivity(intent)
        playMusic()}
}