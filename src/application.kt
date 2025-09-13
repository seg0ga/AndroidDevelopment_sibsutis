import kotlin.random.Random

class Human {
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

    fun move() {
        val direction=Random.nextDouble(0.0, 2*Math.PI)

        x+=currentSpeed*Math.cos(direction)
        y+=currentSpeed*Math.sin(direction)
        println("$name переместился на (${"%.2f".format(x)}; ${"%.2f".format(y)})")}
}

fun main(){
    val people = arrayOf(
        Human("Иван","Иванов","Иванович", 7, 1.1),
        Human("Сергей","Демин","Алексеевич", 42, 1.3),
        Human("Андрей","Кутенков","Алексеевич", 38, 1.5),
        Human("Михаил","Синица","Александрович", 17, 1.7),
        Human("Никита","Криволапов","Алексеевич", 109, 1.9))

    val time_steps = 5

    println("\nЗапуск симуляции на $time_steps секунд")

    for (step in 1..time_steps) {
        println("\nСекунда $step:")
        println("_____________________________________________")
        for (person in people){
            person.move()}}
}