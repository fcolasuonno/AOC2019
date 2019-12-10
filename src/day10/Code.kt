package day10

import isDebug
import java.io.File
import kotlin.math.abs

fun main() {
    val name = if (isDebug()) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) =
    input.mapIndexed { j, s -> s.mapIndexedNotNull { i, c -> if (c == '#') (i to j) else null } }.flatten().toSet()

fun part1(input: Set<Pair<Int, Int>>): Any? = input.map { a ->
    (input - a).map { (it.first - a.first) to (it.second - a.second) }.partition { it.second > 0 }
        .let { (upper, lower) ->
            upper.groupBy { it.second.toDouble() / it.first }.size +
                    lower.groupBy { it.second.toDouble() / it.first }.size
        }
}.max()

fun part2(input: Set<Pair<Int, Int>>) =
    input.map { a ->
        (input - a).partition { it.second < a.second }.let { (upper, lower) ->
            val (upperLeft, upperRight) = upper.partition { it.first < a.first }
            val (lowerLeft, lowerRight) = lower.partition { it.first < a.first }
            listOf(
                upperRight,
                lowerRight,
                lowerLeft,
                upperLeft
            )
                .flatMap {
                    it.groupBy { (it.second - a.second).toDouble() / ((it.first - a.first)) }
                        .toSortedMap()
                        .values
                }
                .map {
                    it.sortedBy { abs(it.second - a.second) + abs(it.first - a.first) }.toMutableList()
                }.toMutableList()
        }
    }.maxBy { it.count() }?.iterator()?.run {
        repeat(200 - 1) {
            next().let {
                it.removeAt(0)
                if (it.isEmpty()) {
                    remove()
                }
            }
        }
        next().first().let { it.first * 100 + it.second }
    }

