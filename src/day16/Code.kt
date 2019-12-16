package day16

import isDebug
import java.io.File
import kotlin.math.abs

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
    it.toList().map { it - '0' }
}.requireNoNulls().first()

fun part1(input: List<Int>) = generateSequence(input) { signal ->
    signal.indices.map { index ->
        val step = 4 * (index + 1)
        abs((index until signal.size step step).sumBy { o ->
            (0..index).sumBy { signal.getOrNull(it + o) ?: 0 }
        } - ((index + (2 * (index + 1))) until signal.size step step).sumBy { o ->
            (0..index).sumBy { signal.getOrNull(it + o) ?: 0 }
        }) % 10
    }
}.take(100 + 1).last().take(8).joinToString("")

fun part2(input: List<Int>): Any? {
    val offset = input.take(7).joinToString("").toInt()
    return generateSequence(List(input.size * 10000 - offset) { input[(offset + it) % input.size] }) { signal ->
        var last = 0
        signal.indices.reversed().map {
            last += signal[it]
            last
        }.map { it % 10 }.reversed()
    }.take(100 + 1).last().take(8).joinToString("")
}
