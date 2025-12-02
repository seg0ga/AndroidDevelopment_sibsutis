package com.example.android_development

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import kotlin.text.String


class sockets : AppCompatActivity() {
    lateinit var serverView: TextView
    lateinit var clientView: TextView
    lateinit var clientText: EditText
    lateinit var sendBttn: Button
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sockets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets}
        serverView=findViewById<TextView>(R.id.serverView)
        clientView=findViewById<TextView>(R.id.clientView)
        sendBttn=findViewById<Button>(R.id.sendBttn)
        clientText=findViewById<EditText>(R.id.client_text)
        handler = Handler(Looper.getMainLooper())

        sendBttn.setOnClickListener{Thread {startClient()}.start()}}



    fun startServer(){
        val context=ZMQ.context(1)
        val socket=ZContext().createSocket(SocketType.REP)
        socket.bind("tcp://localhost:2222")
        var counter: Int = 0

        while(true){
            counter++
            val requestBytes=socket.recv(0)
            val request=String(requestBytes, ZMQ.CHARSET)
            handler.postDelayed({serverView.text=request},0)
            val response="Получено сообщение №$counter"
            socket.send(response.toByteArray(ZMQ.CHARSET),0)}
        socket.close()
        context.close()}

    fun startClient(){
        val context=ZMQ.context(1)
        val socket=ZContext().createSocket(SocketType.REQ)
        socket.connect("tcp://192.168.1.211:2222")

        val request=clientText.text.toString()
        socket.send(request.toByteArray(ZMQ.CHARSET),0)
        val reply=String(socket.recv(0),ZMQ.CHARSET)
        clientView.text=reply

        socket.close()
        context.close()}

    override fun onResume() {
        super.onResume()
//        val runnableServer = Runnable{startServer()}
//        val threadServer = Thread(runnableServer)
//        threadServer.start()
    }
}
