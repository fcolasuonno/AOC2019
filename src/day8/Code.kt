package day8

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

fun parse(input: List<String>) = input.single().toList().map { it - '0' }

fun part1(input: List<Int>): Any? = input.chunked(25 * 6).map { it.groupingBy { it }.eachCount() }
    .minBy { it[0] ?: Int.MAX_VALUE }!!
    .let { it.getOrDefault(1, 0) * it.getOrDefault(2, 0) }

fun part2(input: List<Int>): Any? = input.chunked(25 * 6).reversed()
    .reduce { acc, list -> list.mapIndexed { index, i -> if (i == 2) acc[index] else i } }
    .chunked(25)
    .forEach {
        println(it.joinToString("") { if (it == 1) "#" else " " })
    }
