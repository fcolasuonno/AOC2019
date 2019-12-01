package day1

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
    it.toInt()
}.requireNoNulls()

fun part1(input: List<Int>): Any? = input.sumBy { it/3 - 2 }

fun part2(input: List<Int>): Any? = input.sumBy { mass ->
    generateSequence(mass) { fuel -> (fuel/3 - 2).takeIf { it > 0 } }.drop(1).sum()
}
