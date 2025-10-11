package com.example.calculator.Movable

import kotlin.random.Random

open class Human:Movable,Printable{
    var name: String = ""
        get() = field
        set(value){field =value}

    var surname: String = ""
        get()=field
        set(value){field= value}

    var second_name: String = ""
        get()= field
        set(value){field=value}

    var age: Int = 0
        get()=field
        set(value){
            if (value>0){field=value}
            else {field=0}}

    override var currentSpeed: Double = 0.0

    override var x: Double = 0.0

    override var y: Double = 0.0

    constructor(_name: String, _surname: String, _second: String,  _age: Int, _speed: Double){
        name= _name
        surname= _surname
        second_name= _second
        age= _age
        currentSpeed= _speed}

    override fun printInfo(){println("Создан человек: $surname $name $second_name. Возраст: $age. Скорость $currentSpeed")}

    override fun move() {
        val direction=Random.nextDouble(0.0, 2*Math.PI)

        x+=currentSpeed*Math.cos(direction)
        y+=currentSpeed*Math.sin(direction)
        println("$name перешел на (${"%.2f".format(x)}; ${"%.2f".format(y)})")}}