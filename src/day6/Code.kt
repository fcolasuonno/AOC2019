package day6

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
    it.split(')').let { it[0] to it[1] }
}.requireNoNulls().groupBy({ it.first }) { it.second }

fun part1(input: Map<String, List<String>>) = generateSequence(listOf("COM")) {
    it.mapNotNull { input[it] }.flatten().takeIf { it.isNotEmpty() }
}.map { it.size }.foldIndexed(0) { rank, acc, size -> acc + size * rank }

fun part2(input: Map<String, List<String>>): Any? {
    val reverseMap = input.flatMap { entry -> entry.value.map { it to entry.key } }.toMap()
    val yourOrbits = generateSequence("YOU") { reverseMap[it] }.toSet()
    val santaOrbits = generateSequence("SAN") { reverseMap[it] }.toSet()
    return ((yourOrbits - santaOrbits).size - 1) + ((santaOrbits - yourOrbits).size - 1)
}
