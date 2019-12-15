package day15

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

enum class Movement(val value: Long) {
    N(1L),
    S(2L),
    W(3L),
    E(4L)
}

enum class Tile(val value: Long) {
    Wall(0L),
    Space(1L),
    Oxy(2L),
}

data class Position(val x: Int, val y: Int, var commands: List<Movement> = emptyList<Movement>()) {

    fun neighbours() = listOf(
        copy(y = y + 1, commands = commands + Movement.N),
        copy(y = y - 1, commands = commands + Movement.S),
        copy(x = x + 1, commands = commands + Movement.E),
        copy(x = x - 1, commands = commands + Movement.W)
    )

    fun pos(): Pair<Int, Int> = x to y
}

fun parse(input: List<String>) = input.map {
    it.split(",").map { it.toLong() }
}.requireNoNulls().single()

fun part1(input: List<Long>): Any? {
    val frontier = sortedSetOf(compareBy<Position> { it.commands.size }.thenBy { it.x }.thenBy { it.y }).apply {
        add(Position(0, 0))
    }
    val seen = mutableSetOf(0 to 0)
    while (frontier.isNotEmpty()) {
        frontier.first().let { currentPos ->
            frontier.remove(currentPos)
            currentPos.neighbours().filter { it.pos() !in seen }.map {
                val output = mutableListOf<Long>()
                val inputs = it.commands.toMutableList()
                IntCodeComputer(input, IntCode.Input {
                    inputs.removeAt(0).value
                }, IntCode.Output { a ->
                    output.add(a)
                }).runWhile { output.size == it.commands.size }
                val tile = when (output.last()) {
                    0L -> Tile.Wall
                    1L -> Tile.Space
                    2L -> Tile.Oxy
                    else -> throw IllegalAccessError()
                }
                if (tile == Tile.Oxy) return it.commands.size
                if (tile != Tile.Wall) frontier.add(it)
                seen.add(it.pos())
            }
        }
    }
    return 0
}

fun part2(input: List<Long>): Any? {
    val frontier = sortedSetOf(compareBy<Position> { it.x }.thenBy { it.y }).apply {
        add(Position(0, 0))
    }
    val map = mutableMapOf((0 to 0) to Tile.Space)
    while (frontier.isNotEmpty()) {
        frontier.first().let { currentPos ->
            frontier.remove(currentPos)
            currentPos.neighbours().filter { !map.containsKey(it.pos()) }.map {
                val output = mutableListOf<Long>()
                val inputs = it.commands.toMutableList()
                IntCodeComputer(input, IntCode.Input {
                    inputs.removeAt(0).value
                }, IntCode.Output { a ->
                    output.add(a)
                }).runWhile { output.size == it.commands.size }
                val tile = when (output.last()) {
                    0L -> Tile.Wall
                    1L -> Tile.Space
                    2L -> Tile.Oxy
                    else -> throw IllegalAccessError()
                }
                if (tile != Tile.Wall) frontier.add(it)
                map[it.pos()] = tile
            }
        }
    }
    return generateSequence(map.filterValues { it == Tile.Oxy }.keys.toList()) {
        it.takeIf { it.isNotEmpty() }?.flatMap {
            listOf(
                it.copy(first = it.first + 1),
                it.copy(first = it.first - 1),
                it.copy(second = it.second + 1),
                it.copy(second = it.second - 1)
            )
                .filter { map[it] == Tile.Space }
                .onEach { map[it] = Tile.Oxy }
        }
    }.drop(1).count() - 1
}