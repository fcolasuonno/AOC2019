package day5

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

var currentInput = 1L
val opcodes = mapOf(
    1L to IntCode.Compute { a, b -> a + b },
    2L to IntCode.Compute { a, b -> a * b },
    3L to IntCode.Input { currentInput },
    4L to IntCode.Output { a -> currentInput = a },
    5L to IntCode.Jump { a -> a != 0L },
    6L to IntCode.Jump { a -> a == 0L },
    7L to IntCode.Compare { a, b -> a < b },
    8L to IntCode.Compare { a, b -> a == b },
    99L to IntCode.End
)

fun parse(input: List<String>) = input.map {
    it.split(",").map { it.toLong() }
}.requireNoNulls()

fun part1(input: List<List<Long>>) = input.map { orig ->
    val mem = orig.mapIndexed { index, i -> index.toLong() to i }.toMap().toMutableMap()
    generateSequence(0L) { ip ->
        opcodes[(mem[ip] ?: 0) % 100]?.execute(ip, mem, (mem[ip] ?: 0) / 100)
    }.first {
        opcodes[mem[it]] == IntCode.End
    }
    currentInput
}

fun part2(input: List<List<Long>>): Any? = input.map { orig ->
    currentInput = 5
    val mem = orig.mapIndexed { index, i -> index.toLong() to i }.toMap().toMutableMap()
    generateSequence(0L) { ip ->
        opcodes[(mem[ip] ?: 0) % 100]?.execute(ip, mem, (mem[ip] ?: 0) / 100)
    }.first {
        opcodes[mem[it]] == IntCode.End
    }
    currentInput
}