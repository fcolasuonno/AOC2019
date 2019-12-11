package day11

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

enum class Direction(val dx: Int, val dy: Int) {
    U(0, -1) {
        override fun turn(nextDir: Long): Direction = if (nextDir == 0L) L else R
    },
    D(0, 1) {
        override fun turn(nextDir: Long): Direction = if (nextDir == 0L) R else L
    },
    L(-1, 0) {
        override fun turn(nextDir: Long): Direction = if (nextDir == 0L) D else U
    },
    R(1, 0) {
        override fun turn(nextDir: Long): Direction = if (nextDir == 0L) U else D
    };

    abstract fun turn(nextDir: Long): Direction
}

fun part1(input: List<Long>): Any? {
    val grid = mutableMapOf<Pair<Int, Int>, Int>().withDefault { 0 }
    val output = mutableListOf<Long>()
    val seq = generateSequence((0 to 0) to Direction.U) { (currentPos, currentDir) ->
        val nextCol = output.removeAt(0)
        val nextDir = output.removeAt(0)
        grid[currentPos] = nextCol.toInt()
        val newDir = currentDir.turn(nextDir)
        currentPos.copy(currentPos.first + newDir.dx, currentPos.second + newDir.dy) to newDir
    }
    val col = seq.map { grid.getValue(it.first) }.iterator()
    IntCodeComputer(input, IntCode.Input { col.next().toLong() }, IntCode.Output { a -> output.add(a) }).run()
    return grid.size
}

fun part2(input: List<Long>): Any? {
    val grid = mutableMapOf<Pair<Int, Int>, Int>().withDefault { 0 }
    grid[0 to 0] = 1
    val output = mutableListOf<Long>()
    val seq = generateSequence((0 to 0) to Direction.U) { (currentPos, currentDir) ->
        val nextCol = output.removeAt(0)
        val nextDir = output.removeAt(0)
        grid[currentPos] = nextCol.toInt()
        val newDir = currentDir.turn(nextDir)
        currentPos.copy(currentPos.first + newDir.dx, currentPos.second + newDir.dy) to newDir
    }
    val col = seq.map { grid.getValue(it.first) }.iterator()
    IntCodeComputer(input, IntCode.Input { col.next().toLong() }, IntCode.Output { a -> output.add(a) }).run()
    val xRange = grid.keys.map { it.first }.let { it.min()!!..it.max()!! }
    return grid.keys.map { it.second }.let { it.min()!!..it.max()!! }.joinToString("\n", prefix = "\n") { y ->
        xRange.joinToString("") { x -> if (grid.getValue(x to y) == 0) "  " else "##" }
    }
}