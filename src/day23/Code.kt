package day23

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
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) = input.map {
    it.split(",").map { it.toLong() }
}.requireNoNulls().single()

fun part1(input: List<Long>): Long {
    val network = mutableMapOf(255 to mutableListOf<Long>())
    val computers = (0 until 50).map { address ->
        val inQueue = network.getOrPut(address) { mutableListOf() }
        inQueue.add(address.toLong())
        val outQueue = mutableListOf<Long>()
        IntCodeComputer(input, IntCode.Input { inQueue.removeAt(0) }, IntCode.Output {
            outQueue.add(it)
            if (outQueue.size == 3) {
                network.getOrPut(outQueue[0].toInt()) { mutableListOf() }.let {
                    it.add(outQueue[1])
                    it.add(outQueue[2])
                }
                outQueue.clear()
            }
        }, id = address)
    }
    while (network[255]?.size != 2) {
        if (computers.map { network.getValue(it.id) }.all { it.isEmpty() }) {
            network.getValue(computers.random().id).add(-1L)
        }
        computers.first { comp -> network.getValue(comp.id).isNotEmpty() }.runWhile {
            peekOp is IntCode.Input && network.getValue(id).isEmpty()
        }
    }
    return network.getValue(255)[1]
}

fun part2(input: List<Long>): Long {
    val network = mutableMapOf<Int, MutableList<Long>>()
    val nat = mutableListOf<Long>()
    val computers = (0 until 50).map { address ->
        val inQueue = network.getOrPut(address) { mutableListOf() }
        inQueue.add(address.toLong())
        val outQueue = mutableListOf<Long>()
        IntCodeComputer(input, IntCode.Input { inQueue.removeAt(0) }, IntCode.Output {
            outQueue.add(it)
            if (outQueue.size == 3) {
                val dest = outQueue[0].toInt()
                if (dest == 255) {
                    nat.clear()
                    nat.add(outQueue[1])
                    nat.add(outQueue[2])
                } else network.getOrPut(dest) { mutableListOf() }.let {
                    it.add(outQueue[1])
                    it.add(outQueue[2])
                }
                outQueue.clear()
            }
        }, id = address)
    }
    var oldNat = 0L
    while (network.any { it.value.isNotEmpty() } || nat.isEmpty() || oldNat != nat[1]) {
        if (network.all { it.value.isEmpty() }) {
            if (nat.isNotEmpty()) {
                oldNat = nat[1]
                network.getValue(0).addAll(nat)
            } else network.values.random().add(-1L)
        }
        computers.first { comp -> network.getValue(comp.id).isNotEmpty() }
            .runWhile { peekOp is IntCode.Input && network.getValue(id).isEmpty() }
    }
    return oldNat
}
