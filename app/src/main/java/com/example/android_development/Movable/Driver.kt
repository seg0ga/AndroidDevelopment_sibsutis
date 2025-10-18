package com.example.calculator.Movable

class Driver:Human,Printable {
    var angle:Double = 0.0
        get()=field
        set(value){field=value%(2*Math.PI)}

    constructor(_name:String,_surname:String,_second:String,_age:Int,_speed: Double,_direction: Double):
            super(_name, _surname, _second, _age, _speed) {
        angle=_direction}

    override fun printInfo(){println("Создан водитель: $surname $name $second_name. Направление: ${"%.2f".format(Math.toDegrees(angle))}°. Скорость: $currentSpeed")}

    override fun move() {
        x+=currentSpeed*Math.cos(angle)
        y+=currentSpeed*Math.sin(angle)
        println("$name переехал на (${"%.2f".format(x)}; ${"%.2f".format(y)})")}}