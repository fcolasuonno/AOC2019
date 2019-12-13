package day13

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
    val output = mutableListOf<Long>()
    IntCodeComputer(input, IntCode.Input { 0 }, IntCode.Output { a -> output.add(a) }).run()
    return output.chunked(3).count { it[2] == 2L }
}

fun part2(input: List<Long>): Any? {
    val output = mutableListOf<Long>()
    val screen = mutableMapOf<Pair<Long, Long>, Long>().withDefault { 0 }
    var paddle = 0L
    var ball = 0L
    IntCodeComputer(input, IntCode.Input {
        if (isDebug()) {
            System.err.println((0L..24L).joinToString("\n", prefix = "\n") { y ->
                (0L..39L).joinToString("") { x ->
                    val p = screen.getValue(x to y)
                    when (p) {
                        0L -> " "
                        else -> p.toString()
                    }
                }
            })
        }
        (ball - paddle).coerceIn(-1L..1L)
    }, IntCode.Output { a ->
        output.add(a)
        if (output.size == 3) {
            val (x, y, tile) = output
            screen[x to y] = tile
            if (tile == 4L) {
                ball = x
            }
            if (tile == 3L) {
                paddle = x
            }
            output.clear()
        }
    }).apply {
        mem[0] = 2
    }.run()
    return screen[-1L to 0L]
}