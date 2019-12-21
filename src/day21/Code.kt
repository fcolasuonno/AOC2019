package day21

import IntCode
import IntCodeComputer
import isDebug
import java.io.File

fun main() {
    val name = if (isDebug()) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) = input.map {
    it.split(",").map { it.toLong() }
}.requireNoNulls().single()

fun part1(input: List<Long>): Any? {
//    val (a,b,c,d) = listOf(false,false,false,false)
//    if(!a || !(!d || b) || !(!d || c)){}
    val springcode = ("NOT A J\n" +
            "NOT D T\n" +
            "OR B T\n" +
            "NOT T T\n" +
            "OR T J\n" +
            "NOT D T\n" +
            "OR C T\n" +
            "NOT T T\n" +
            "OR T J\n" +
            "WALK\n").map {
        it.toLong()
    }.toMutableList()
    val output = mutableListOf<Long>()
    IntCodeComputer(input, IntCode.Input { springcode.removeAt(0) }, IntCode.Output { a -> output.add(a) }).run()
    return output.single { it > 255 }
}

fun part2(input: List<Long>): Any? {
//    val (a,b,c,d) = listOf(false,false,false,false)
//    val (e,f,g,h,i) = listOf(false,false,false,false,false)
//    if(!a || !(!d || b) || (!c && d && h)){}
    val springcode = ("NOT A J\n" +
            "NOT D T\n" +
            "OR B T\n" +
            "NOT T T\n" +
            "OR T J\n" +
            "NOT C T\n" +
            "AND D T\n" +
            "AND H T\n" +
            "OR T J\n" +
            "RUN\n").map {
        it.toLong()
    }.toMutableList()
    val output = mutableListOf<Long>()
    IntCodeComputer(input, IntCode.Input { springcode.removeAt(0) }, IntCode.Output { a -> output.add(a) }).run()
    return output.single { it > 255 }
}