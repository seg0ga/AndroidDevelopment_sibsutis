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
import android.os.Environment
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.widget.ArrayAdapter
import android.widget.ListView

class mediaplayer : AppCompatActivity() {
    var play_p: Boolean = false
    lateinit var bttn_play: ImageButton
    lateinit var name: TextView
    lateinit var seekBar: SeekBar
    lateinit var audio: MutableList<File>
    lateinit var mediaplayer: MediaPlayer
    lateinit var songsListView: ListView
    var count: Int = 0
    val handler = Handler()
    lateinit var currentTimeText: TextView
    lateinit var totalTimeText: TextView
    lateinit var volumeSeekBar: SeekBar
    lateinit var audioManager: AudioManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mediaplayer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets}

        bttn_play=findViewById(R.id.bttn_play)
        var bttn_forward=findViewById<ImageButton>(R.id.bttn_forward)
        var bttn_back=findViewById<ImageButton>(R.id.bttn_back)
        var bttn_stop=findViewById<ImageButton>(R.id.bttn_stop)
        name=findViewById(R.id.textView_name)
        seekBar=findViewById(R.id.seekBar)
        currentTimeText=findViewById(R.id.currentTimeText)
        totalTimeText=findViewById(R.id.totalTimeText)
        volumeSeekBar=findViewById(R.id.volumeseekbar)
        audioManager=getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mediaplayer=MediaPlayer()
        audio=mutableListOf()
        setupVolumeSeekBar()

        bttn_play.setOnClickListener{play()}
        bttn_forward.setOnClickListener{nextTrack()}
        bttn_back.setOnClickListener{predTrack()}
        bttn_stop.setOnClickListener {
            mediaplayer.pause()
            mediaplayer.seekTo(0)
            play_p = false
            bttn_play.setImageResource(R.drawable.pause)
            seekBar.progress = 0
            currentTimeText.text = "0:00"}

        seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar:SeekBar,progress:Int,fromUser:Boolean){
                if (fromUser) {currentTimeText.text = formatTime(progress)}}

            override fun onStartTrackingTouch(seekBar: SeekBar) {handler.removeCallbacksAndMessages(null)}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                mediaplayer.seekTo(seekBar.progress)
                updateSeekBar()
                if (play_p) {mediaplayer.start()}}})

        val requestPermessionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadInitialMusic()}}
        requestPermessionLauncher.launch(READ_MEDIA_AUDIO)
        songsListView = findViewById(R.id.songsListView)}





    fun setupVolumeSeekBar() {
        val maxVolume=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume=audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        volumeSeekBar.max=maxVolume
        volumeSeekBar.progress=currentVolume

        volumeSeekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)}}

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}}) }



    fun formatTime(millis: Int):String{
        val totalSeconds=millis/1000
        val minutes=totalSeconds/60
        val seconds=totalSeconds%60
        return String.format("%d:%02d",minutes,seconds)}



    fun updateSeekBar(){
        handler.postDelayed({
            if (mediaplayer.isPlaying) {
                val currentPos =mediaplayer.currentPosition
                seekBar.progress=currentPos
                currentTimeText.text =formatTime(currentPos)
                updateSeekBar()}}, 1000)}



    fun loadInitialMusic() {
        val myMusicDir=File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "MyMusic")

        if (!myMusicDir.exists()){myMusicDir.mkdirs()}

        if (myMusicDir.exists()&&myMusicDir.isDirectory) {
            myMusicDir.listFiles()?.forEach { file ->
                if (file.isFile && file.name.endsWith(".mp3", ignoreCase = true)) {audio.add(file)}}}

        if (audio.size > 0) {
            val songNames=audio.map{it.name}
            val adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,songNames)
            songsListView.adapter=adapter

            songsListView.setOnItemClickListener{parent,view,position,id ->
                count=position
                playTrack(count)
                if (play_p) {mediaplayer.start()}}
            playTrack(0)
        }else{ name.text = "Нет mp3 в папке Music/MyMusic/" }}




    fun playTrack(index: Int){
        mediaplayer.reset()
        mediaplayer.setDataSource(audio[index].absolutePath)
        mediaplayer.prepare()
        name.text=audio[index].name
        val duration=mediaplayer.duration
        seekBar.max=duration
        totalTimeText.text=formatTime(duration)
        currentTimeText.text="0:00"
        seekBar.progress=0

        mediaplayer.setOnCompletionListener {nextTrack()}
        if (play_p) {updateSeekBar()}}



    fun play() {
        if (play_p==false) {
            mediaplayer.start()
            bttn_play.setImageResource(R.drawable.play)
            play_p=true
            updateSeekBar()
        } else {
            mediaplayer.pause()
            bttn_play.setImageResource(R.drawable.pause)
            play_p=false}}



    fun nextTrack() {
        if (audio.isNotEmpty()) {
            count=(count+1)%audio.size
            playTrack(count)
            if (play_p){
                mediaplayer.start()
                updateSeekBar()}}}



    fun predTrack(){
        if (audio.isNotEmpty()){
            if (count-1<0){count=audio.size-1 }
            else{count=count-1 }
            playTrack(count)
            if (play_p){
                mediaplayer.start()
                updateSeekBar()}}}}