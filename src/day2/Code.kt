package day2

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
    it.split(",").map { it.toInt() }
}.requireNoNulls()

fun part1(input: List<List<Int>>) =
    input.map { orig ->
        val codes = orig.toMutableList()
        codes[1] = 12
        codes[2] = 2
        var pos = 0
        while (pos < codes.size && codes[pos] != 99) {
            when (codes[pos]) {
                1 -> codes[codes[pos + 3]] = codes[codes[pos + 1]] + codes[codes[pos + 2]]
                2 -> codes[codes[pos + 3]] = codes[codes[pos + 1]] * codes[codes[pos + 2]]
            }
            pos += 4
        }
        codes.first()
    }


fun part2(input: List<List<Int>>): Any? = input.map { oorigCodes ->
    for (noun in 0..99) {
        for (verb in 0..99) {
            val codes = oorigCodes.toMutableList()
            codes[1] = noun
            codes[2] = verb
            var pos = 0
            while (pos < codes.size && codes[pos] != 99) {
                when (codes[pos]) {
                    1 -> codes[codes[pos + 3]] = codes[codes[pos + 1]] + codes[codes[pos + 2]]
                    2 -> codes[codes[pos + 3]] = codes[codes[pos + 1]] * codes[codes[pos + 2]]
                }
                pos += 4
            }
            if (codes.first() == 19690720) {
                return@map codes[1] * 100 + codes[2]
            }
        }
    }
}