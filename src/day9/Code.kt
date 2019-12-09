package day9

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
    val inputs = mutableListOf(1L)
    val output = mutableListOf<Long>()
    val computer = IntCodeComputer(input, IntCode.Input { inputs.removeAt(0) },
        IntCode.Output { a -> output.add(a) })
    computer.run()
    return output
}

fun part2(input: List<Long>): Any? {
    val inputs = mutableListOf(2L)
    val output = mutableListOf<Long>()
    val computer = IntCodeComputer(input, IntCode.Input { inputs.removeAt(0) },
        IntCode.Output { a -> output.add(a) })
    computer.run()
    return output
}
