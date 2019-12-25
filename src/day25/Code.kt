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
     *
     *  maze:
     *
    Hull Breach
    N-Corridor
    E-Storage
    W-Sick Bay

    Sick Bay
    E-Hull Breach
    W-Hot Chocolate Fountain

    Hot Chocolate Fountain
    N-Warp Drive Maintenance
    E-Sick Bay
    W-Crew Quarters

    Crew Quarters
    E-Hot Chocolate Fountain

    Warp Drive Maintenance
    S-Hot Chocolate Fountain

    Corridor                cake
    N-Stables
    E-Passages
    S-Hull Breach

    Passages
    N-Gift Wrapping Center
    E-Arcade
    W-Corridor

    Arcade
    N-Navigation
    W-Passages

    Navigation      pointer
    S-Arcade

    Gift Wrapping Center
    S-Passages

    Stables            mutex
    E-Engineering
    S-Corridor
    W-Kitchen

    Engineering         antenna
    W-Stables

    Kitchen
    E-Stables

    Storage
    W-Hull Breach
    E-Observatory

    Observatory        tambourine
    N-Science Lab
    E-Holodeck
    W-Storage

    Science Lab
    S-Observatory

    Holodeck     fuel cell
    E-Hallway
    W-Observatory

    Hallway       boulder
    N-Security Checkpoint
    W-88

    Security Checkpoint
    E-Pressure-Sensitive Floor
    S-Hallway

    Pressure-Sensitive Floor
    W-Security Checkpoint
     */
    while (!inputCommand.joinToString("").startsWith("quit")) {
        comp.runWhile { peekOp is IntCode.Input && inputCommand.isEmpty() }
        readLine()?.let {
            inputCommand.addAll(it.toList() + '\n')
        }
    }
}
