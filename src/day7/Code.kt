package day7

import IntCode
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

val mainOpcodes = mapOf(
    1L to IntCode.Compute { a, b -> a + b },
    2L to IntCode.Compute { a, b -> a * b },
    5L to IntCode.Jump { a -> a != 0L },
    6L to IntCode.Jump { a -> a == 0L },
    7L to IntCode.Compare { a, b -> a < b },
    8L to IntCode.Compare { a, b -> a == b },
    99L to IntCode.End
)

fun part1(input: List<Long>) = setOf(0L, 1L, 2L, 3L, 4L).permutations.map { phases ->
    phases.fold(0L) { inputVal, phase ->
        val inputs = mutableListOf(phase, inputVal)
        var output = 0L
        val opcodes = mainOpcodes + mapOf(
            3L to IntCode.Input { inputs.removeAt(0) },
            4L to IntCode.Output { a -> output = a })
        val mem = input.mapIndexed { index, i -> index.toLong() to i }.toMap().toMutableMap()
        generateSequence(0L) { ip ->
            opcodes[(mem[ip] ?: 0) % 100]?.execute(ip, mem, (mem[ip] ?: 0) / 100)
        }.first {
            opcodes[mem[it]] == IntCode.End
        }
        output
    }
}.max()

fun part2(program: List<Long>): Any? = setOf(9L, 8L, 7L, 6L, 5L).permutations.map { phases ->
    val initialInput = phases.map { phase -> mutableListOf(phase) }
    initialInput.first().add(0)
    Amplifiers(initialInput.mapIndexed { i, input ->
        val mem = program.mapIndexed { index, i -> index.toLong() to i }.toMap().toMutableMap()
        val opcodes = mainOpcodes + mapOf(
            3L to IntCode.Input { input.removeAt(0) },
            4L to IntCode.Output { a -> initialInput[(i + 1) % initialInput.size].add(a) })
        var wantsInput = false
        Amplifier(generateSequence(0L) { ip ->
            opcodes[(mem[ip] ?: 0) % 100]
                ?.takeIf { it != IntCode.End }
                ?.execute(ip, mem, (mem[ip] ?: 0) / 100)
                .also {
                    wantsInput = it != null && opcodes[(mem[it] ?: 0) % 100] is IntCode.Input
                }
        }.iterator()) { input.isNotEmpty() || !wantsInput }
    }).forEachRemaining {
        //do nothing
    }
    initialInput.first().single()
}.max()

data class Amplifier(val iterator: Iterator<Long>, val canProceed: () -> Boolean) : Iterator<Unit> {
    override fun hasNext() = canProceed() && iterator.hasNext()

    override fun next() {
        iterator.next()
    }
}

data class Amplifiers(val amplifiers: List<Amplifier>) : Iterator<Unit> {
    override fun hasNext() = amplifiers.any { it.hasNext() }

    override fun next() = amplifiers.first { it.hasNext() }.next()
}
