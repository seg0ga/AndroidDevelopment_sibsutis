import kotlin.random.Random
import kotlin.concurrent.thread

open class Human {
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

    var currentSpeed: Double = 0.0
        get()=field
        set(value){field=value}

    var x: Double = 0.0
        get()=field
        set(value){field=value}

    var y: Double = 0.0
        get()=field
        set(value){field=value}

    constructor(_name: String, _surname: String, _second: String,  _age: Int, _speed: Double){
        name= _name
        surname= _surname
        second_name= _second
        age= _age
        currentSpeed= _speed
        println("Создан человек: $surname $name $second_name. Возраст: $age. Скорость $currentSpeed")}

    open fun move() {
        val direction=Random.nextDouble(0.0, 2*Math.PI)

        x+=currentSpeed*Math.cos(direction)
        y+=currentSpeed*Math.sin(direction)
        println("$name перешел на (${"%.2f".format(x)}; ${"%.2f".format(y)})")}}

class Driver:Human {
    var angle:Double = 0.0
        get()=field
        set(value){field=value%(2*Math.PI)}

    constructor(_name:String,_surname:String,_second:String,_age:Int,_speed: Double,_direction: Double):
            super(_name, _surname, _second, _age, _speed) {
        angle=_direction
        println("Создан водитель: $surname $name $second_name. Направление: ${"%.2f".format(Math.toDegrees(angle))}°. Скорость: $currentSpeed")}

    override fun move() {
        x+=currentSpeed*Math.cos(angle)
        y+=currentSpeed*Math.sin(angle)
        println("$name переехал на (${"%.2f".format(x)}; ${"%.2f".format(y)})")}}

fun main(){
    val people=arrayOf(
        Human("Иван","Иванов","Иванович", 7, 1.1),
        Human("Сергей","Демин","Алексеевич", 42, 1.3),
        Human("Андрей","Кутенков","Алексеевич", 38, 1.5),
        Human("Михаил","Синица","Александрович", 17, 1.7),
        Driver("Никита","Криволапов","Алексеевич",109,15.0,Math.PI/4))

    val time_steps = 5

    println("\nЗапуск симуляции на $time_steps секунд")

    for (step in 1..time_steps) {
        println("\nСекунда $step:")
        println("_____________________________________________")

        val threads = mutableListOf<Thread>()

        for (person in people){
            val thread=thread{
                person.move()}
            threads.add(thread)}
        threads.forEach { it.join() }}}