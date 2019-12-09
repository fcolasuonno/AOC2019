package day2

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
    IntCodeComputer(orig).let {
        it.mem[1] = 12
        it.mem[2] = 2
        it.run()
        it.mem[0]
    }
}

fun part2(input: List<List<Long>>): Any? = input.map { orig ->
    (0..99).flatMap { noun -> (0..99).map { verb -> noun.toLong() to verb.toLong() } }.first { (noun, verb) ->
        IntCodeComputer(orig).let {
            it.mem[1] = noun
            it.mem[2] = verb
            it.run()
            it.mem[0] == 19690720L
        }
    }.let { it.first * 100 + it.second }
}