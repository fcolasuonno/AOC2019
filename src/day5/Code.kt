package day5

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

var currentInput = 1
val opcodes = mapOf(
    1 to IntCode.Compute { a, b -> a + b },
    2 to IntCode.Compute { a, b -> a * b },
    3 to IntCode.Input { currentInput },
    4 to IntCode.Output { a -> currentInput = a },
    5 to IntCode.Jump { a -> a != 0 },
    6 to IntCode.Jump { a -> a == 0 },
    7 to IntCode.Compare { a, b -> a < b },
    8 to IntCode.Compare { a, b -> a == b },
    99 to IntCode.End
)

fun parse(input: List<String>) = input.map {
    it.split(",").map { it.toInt() }
}.requireNoNulls()

fun part1(input: List<List<Int>>) = input.map { orig ->
    val mem = orig.toMutableList()
    generateSequence(0) { ip ->
        opcodes[mem[ip] % 100]?.execute(ip, mem, mem[ip] / 100)
    }.first {
        opcodes[mem[it]] == IntCode.End
    }
    currentInput
}

fun part2(input: List<List<Int>>): Any? = input.map { orig ->
    currentInput = 5
    val mem = orig.toMutableList()
    generateSequence(0) { ip ->
        opcodes[mem[ip] % 100]?.execute(ip, mem, mem[ip] / 100)
    }.first {
        opcodes[mem[it]] == IntCode.End
    }
    currentInput
}