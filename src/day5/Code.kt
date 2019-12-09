package day5

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
}.requireNoNulls()

fun part1(input: List<List<Long>>) = input.map { orig ->
    var currentInput = 1L
    IntCodeComputer(orig, IntCode.Input { currentInput }, IntCode.Output { currentInput = it }).run()
    currentInput
}

fun part2(input: List<List<Long>>): Any? = input.map { orig ->
    var currentInput = 5L
    IntCodeComputer(orig, IntCode.Input { currentInput }, IntCode.Output { currentInput = it }).run()
    currentInput
}