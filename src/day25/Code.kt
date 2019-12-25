package day25

import IntCode
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
}

fun parse(input: List<String>) = input.map {
    it.split(",").map { it.toLong() }
}.requireNoNulls().single()

fun part1(input: List<Long>) {
    val inputCommand = mutableListOf<Char>()
    val comp = IntCodeComputer(input, IntCode.Input { inputCommand.removeAt(0).toLong() }, IntCode.Output {
        print(it.toChar())
    })
    /** play the game, avoid:
     *   giant electromagnet
     *   escape pod
     *   infinite loop
     *   photons
     *   molten lava
     *
     *
     *  take:
     *  boulder
     *  cake
     *  coin
     *  antenna
     *
     *  drop:
     *  pointer
     *  fuel cell
     *  tambourine
     *  mutex
     */
    while (!inputCommand.joinToString("").startsWith("quit")) {
        comp.runWhile { peekOp is IntCode.Input && inputCommand.isEmpty() }
        readLine()?.let {
            inputCommand.addAll(it.toList() + '\n')
        }
    }
}
