package day7

import IntCode
import IntCodeComputer
import isDebug
import permutations
import java.io.File
import java.util.concurrent.LinkedTransferQueue
import kotlin.concurrent.thread

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

fun part1(input: List<Long>) = setOf(0L, 1L, 2L, 3L, 4L).permutations.map { phases ->
    phases.fold(0L) { inputVal, phase ->
        val inputs = mutableListOf(phase, inputVal)
        var output = 0L
        IntCodeComputer(input, IntCode.Input { inputs.removeAt(0) }, IntCode.Output { a -> output = a }).run()
        output
    }
}.max()

fun part2(program: List<Long>): Any? = setOf(9L, 8L, 7L, 6L, 5L).permutations.map { phases ->
    val initialInput = phases.mapIndexed { i, phase ->
        LinkedTransferQueue<Long>().apply {
            put(phase)
            if (i == 0) put(0)
        }
    }
    initialInput.mapIndexed { i, input ->
        thread(start = true) {
            IntCodeComputer(
                program,
                IntCode.Input { input.take() },
                IntCode.Output { a -> initialInput[(i + 1) % initialInput.size].put(a) },
                id = i
            ).run()
        }
    }.forEach { it.join() }

    initialInput.flatten().single()
}.max()
