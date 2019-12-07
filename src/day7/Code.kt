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
    it.split(",").map { it.toInt() }
}.requireNoNulls().single()

val mainOpcodes = mapOf(
    1 to IntCode.Compute { a, b -> a + b },
    2 to IntCode.Compute { a, b -> a * b },
    5 to IntCode.Jump { a -> a != 0 },
    6 to IntCode.Jump { a -> a == 0 },
    7 to IntCode.Compare { a, b -> a < b },
    8 to IntCode.Compare { a, b -> a == b },
    99 to IntCode.End
)

fun part1(input: List<Int>) = setOf(0, 1, 2, 3, 4).permutations.map { phases ->
    phases.fold(0) { inputVal, phase ->
        val inputs = mutableListOf(phase, inputVal)
        var output = 0
        val opcodes = mainOpcodes + mapOf(
            3 to IntCode.Input { inputs.removeAt(0) },
            4 to IntCode.Output { a -> output = a })
        val mem = input.toMutableList()
        generateSequence(0) { ip ->
            opcodes[mem[ip] % 100]?.execute(ip, mem, mem[ip] / 100)
        }.first {
            opcodes[mem[it]] == IntCode.End
        }
        output
    }
}.max()

fun part2(program: List<Int>): Any? = setOf(9, 8, 7, 6, 5).permutations.map { phases ->
    val initialInput = phases.map { phase -> mutableListOf(phase) }
    initialInput.first().add(0)
    Amplifiers(initialInput.mapIndexed { i, input ->
        val mem = program.toMutableList()
        val opcodes = mainOpcodes + mapOf(
            3 to IntCode.Input { input.removeAt(0) },
            4 to IntCode.Output { a -> initialInput[(i + 1) % initialInput.size].add(a) })
        var wantsInput = false
        Amplifier(generateSequence(0) { ip ->
            opcodes[mem[ip] % 100]
                ?.takeIf { it != IntCode.End }
                ?.execute(ip, mem, mem[ip] / 100)
                .also {
                    wantsInput = it != null && opcodes[mem[it] % 100] is IntCode.Input
                }
        }.iterator()) { input.isNotEmpty() || !wantsInput }
    }).forEachRemaining {
        //do nothing
    }
    initialInput.first().single()
}.max()

data class Amplifier(val iterator: Iterator<Int>, val canProceed: () -> Boolean) : Iterator<Unit> {
    override fun hasNext() = canProceed() && iterator.hasNext()

    override fun next() {
        iterator.next()
    }
}

data class Amplifiers(val amplifiers: List<Amplifier>) : Iterator<Unit> {
    override fun hasNext() = amplifiers.any { it.hasNext() }

    override fun next() = amplifiers.first { it.hasNext() }.next()
}
