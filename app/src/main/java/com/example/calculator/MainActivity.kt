package com.example.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    var digit1:String=""
    var digit2:String=""
    var operator:String=""
    lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets}

        result=findViewById(R.id.result)


        val bttn0=findViewById<Button>(R.id.bttn0)
        val bttn1=findViewById<Button>(R.id.bttn1)
        val bttn2=findViewById<Button>(R.id.bttn2)
        val bttn3=findViewById<Button>(R.id.bttn3)
        val bttn4=findViewById<Button>(R.id.bttn4)
        val bttn5=findViewById<Button>(R.id.bttn5)
        val bttn6=findViewById<Button>(R.id.bttn6)
        val bttn7=findViewById<Button>(R.id.bttn7)
        val bttn8=findViewById<Button>(R.id.bttn8)
        val bttn9=findViewById<Button>(R.id.bttn9)

        val bttn_plus=findViewById<Button>(R.id.bttn_plus)
        val bttn_minus=findViewById<Button>(R.id.bttn_minus)
        val bttn_umn=findViewById<Button>(R.id.bttn_umn)
        val bttn_del=findViewById<Button>(R.id.bttn_del)
        val bttn_ravn=findViewById<Button>(R.id.bttn_ravn)
        val bttn_clear=findViewById<Button>(R.id.bttn_clear)

        bttn0.setOnClickListener{addDigit("0")}
        bttn1.setOnClickListener{addDigit("1")}
        bttn2.setOnClickListener{addDigit("2")}
        bttn3.setOnClickListener{addDigit("3")}
        bttn4.setOnClickListener{addDigit("4")}
        bttn5.setOnClickListener{addDigit("5")}
        bttn6.setOnClickListener{addDigit("6")}
        bttn7.setOnClickListener{addDigit("7")}
        bttn8.setOnClickListener{addDigit("8")}
        bttn9.setOnClickListener{addDigit("9")}

        bttn_plus.setOnClickListener{addOperator("+")}
        bttn_minus.setOnClickListener{addOperator("-")}
        bttn_umn.setOnClickListener{addOperator("*")}
        bttn_del.setOnClickListener{addOperator("/")}
        bttn_ravn.setOnClickListener{calculate()}
        bttn_clear.setOnClickListener{clear()}
    }
    fun addDigit(digit:String){
        if (operator.isEmpty()){digit1+=digit;result.text=digit1}
        else {digit2+=digit;result.text="$digit1$operator$digit2"}}

    fun addOperator(oper: String){
        if (digit1.isNotEmpty()){operator=oper;result.text="$digit1$operator"}}

    fun calculate(){
        if (digit1.isNotEmpty()&&operator.isNotEmpty()&&digit2.isNotEmpty()){
            val number1=digit1.toDouble()
            val number2=digit2.toDouble()
            var res:Double=0.0
            var resText: String=""

            if (operator=="+"){res=(number1+number2)}
            else if (operator=="-"){res=(number1-number2)}
            else if (operator=="*"){res=(number1*number2)}
            else {res=(number1/number2)}

            if (res%1==0.0){resText=res.toInt().toString()}
            else {resText=res.toString()}

            result.text=resText
            digit1=resText
            digit2=""
            operator=""}}

    fun clear(){
        digit1=""
        digit2=""
        operator=""
        result.text="0"}
}