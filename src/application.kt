import kotlin.concurrent.thread

fun main(){
    val people=arrayOf(
        Human("Иван","Иванов","Иванович", 7, 1.1),
        Human("Сергей","Демин","Алексеевич", 42, 1.3),
        Human("Андрей","Кутенков","Алексеевич", 38, 1.5),
        Human("Михаил","Синица","Александрович", 17, 1.7),
        Driver("Никита","Криволапов","Алексеевич",109,15.0,Math.PI/6))

    for (person in people){person.printInfo()}

    val time_steps=5

    println("\nЗапуск симуляции на $time_steps секунд")

    for (step in 1..time_steps) {
        println("\nСекунда $step:")
        println("_________")

        val threads = mutableListOf<Thread>()

        for (person in people){
            val thread=thread{
                person.move()}
            threads.add(thread)}
        threads.forEach { it.join() }}}