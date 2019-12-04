package day4

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

private val lineStructure = """(\d+)-(\d+)""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (start, end) = it.toList()
        (start.toInt())..(end.toInt())
    }
}.requireNoNulls().first()

fun part1(input: IntRange): Any? = input.count { candidatePW ->
    val pairs = candidatePW.toString().zipWithNext()
    pairs.all { it.first <= it.second }
            && pairs.any { it.first == it.second }
}

fun part2(input: IntRange): Any? = input.count { candidatePW ->
    val pairs = candidatePW.toString().zipWithNext()
    pairs.all { it.first <= it.second }
            && pairs.filter { it.first == it.second }.groupingBy { it.first }.eachCount().containsValue(1)
}