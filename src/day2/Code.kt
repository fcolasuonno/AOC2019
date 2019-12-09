package day2

import IntCode
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

val opcodes = mapOf(
    1L to IntCode.Compute { a, b -> a + b },
    2L to IntCode.Compute { a, b -> a * b },
    99L to IntCode.End
)

fun parse(input: List<String>) = input.map {
    it.split(",").map { it.toLong() }
}.requireNoNulls()

fun part1(input: List<List<Long>>) = input.map { orig ->
    val mem = orig.mapIndexed { index, i -> index.toLong() to i }.toMap().toMutableMap()
    mem[1] = 12
    mem[2] = 2
    generateSequence(0L) { ip -> opcodes[mem[ip]]?.execute(ip, mem) }.first { opcodes[mem[it]] == IntCode.End }
    mem[0]
}

fun part2(input: List<List<Long>>): Any? = input.map { orig ->
    (0..99).flatMap { noun -> (0..99).map { verb -> noun to verb } }.first { (noun, verb) ->
        val mem = orig.mapIndexed { index, i -> index.toLong() to i }.toMap().toMutableMap()
        mem[1] = noun.toLong()
        mem[2] = verb.toLong()
        generateSequence(0L to opcodes.getValue(mem[0] ?: 0)) { (ip, op) ->
            op.execute(ip, mem).let { it to opcodes.getValue(mem[it] ?: 0) }
        }.first { it.second == IntCode.End }
        mem[0] == 19690720L
    }.let { it.first * 100 + it.second }
}