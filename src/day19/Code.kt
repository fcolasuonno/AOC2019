package day19

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

fun part1(input: List<Long>) = (0..49).sumBy { j ->
    (0..49).count { i ->
        input.isBeamed(i, j).also {
            print(if (it) '#' else '.')
        }
    }.also {
        println()
    }
}

fun part2(input: List<Long>) = (800..1000).flatMap { j ->
    ((j * 3 / 4 - 3)..(j * 3 / 4 + 150)).map { it to j }
}.first {
    input.isBeamed(it.first + 99, it.second) && input.isBeamed(it.first, it.second + 99)
}.let { it.first * 10000 + it.second }

private fun List<Long>.isBeamed(i: Int, j: Int): Boolean {
    val coord = mutableListOf(i, j)
    var output = 0
    IntCodeComputer(
        this,
        IntCode.Input { coord.removeAt(0).toLong() },
        IntCode.Output { a -> output = a.toInt() }).run()
    return output == 1
}
