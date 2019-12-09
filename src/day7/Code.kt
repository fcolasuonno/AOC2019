package day7

import IntCode
import IntCodeComputer
import isDebug
import permutations
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

fun part1(input: List<Long>) = setOf(0L, 1L, 2L, 3L, 4L).permutations.map { phases ->
    phases.fold(0L) { inputVal, phase ->
        val inputs = mutableListOf(phase, inputVal)
        var output = 0L
        IntCodeComputer(input, IntCode.Input { inputs.removeAt(0) }, IntCode.Output { a -> output = a }).run()
        output
    }
}.max()

fun part2(program: List<Long>): Any? = setOf(9L, 8L, 7L, 6L, 5L).permutations.map { phases ->
    val initialInput = phases.map { phase -> mutableListOf(phase) }
    initialInput.first().add(0)
    val amplifiers = Amplifiers(initialInput.mapIndexed { i, input ->
        Amplifier(IntCodeComputer(program, IntCode.Input { input.removeAt(0) },
            IntCode.Output { a -> initialInput[(i + 1) % initialInput.size].add(a) }, id = i
        ), input
        )
    })
    amplifiers.forEachRemaining {
        //do nothing
    }
    initialInput.flatten().single()
}.max()

data class Amplifier(val computer: IntCodeComputer, val input: List<Long>) : Iterator<Unit> {
    private val sequenceIterator = computer.sequence.iterator()
    override fun hasNext() = (input.isNotEmpty() || computer.peekOp !is IntCode.Input) && computer.peekOp != IntCode.End

    override fun next() {
        sequenceIterator.next()
    }
}

data class Amplifiers(val amplifiers: List<Amplifier>) : Iterator<Unit> {
    override fun hasNext() = amplifiers.any { it.hasNext() }

    override fun next() = amplifiers.first { it.hasNext() }.next()
}
