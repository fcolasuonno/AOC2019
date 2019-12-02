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
    1 to IntCode.IndirectAddressed { a, b -> a + b },
    2 to IntCode.IndirectAddressed { a, b -> a * b },
    99 to IntCode.End
)

fun parse(input: List<String>) = input.map {
    it.split(",").map { it.toInt() }
}.requireNoNulls()

fun part1(input: List<List<Int>>) = input.map { orig ->
    val mem = orig.toMutableList()
    mem[1] = 12
    mem[2] = 2
    generateSequence(0) { ip -> opcodes[mem[ip]]?.execute(ip, mem) }.first { opcodes[mem[it]] == IntCode.End }
    mem.first()
}

fun part2(input: List<List<Int>>): Any? = input.map { orig ->
    (0..99).flatMap { noun -> (0..99).map { verb -> noun to verb } }.first { (noun, verb) ->
        val mem = orig.toMutableList()
        mem[1] = noun
        mem[2] = verb
        generateSequence(0 to opcodes.getValue(mem[0])) { (ip, op) ->
            op.execute(ip, mem).let { it to opcodes.getValue(mem[it]) }
        }.first { it.second == IntCode.End }
        mem.first() == 19690720
    }.let { it.first * 100 + it.second }
}