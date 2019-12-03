package day3

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

data class Movement(val d: Char, val steps: Int) {
    fun points(point: Point): List<Point> = (1..steps).map {
        when (d) {
            'U' -> point.copy(y = point.y - it)
            'D' -> point.copy(y = point.y + it)
            'L' -> point.copy(x = point.x - it)
            else -> point.copy(x = point.x + it)
        }
    }
}

data class Point(val x: Int, val y: Int)

fun parse(input: List<String>) = input.map {
    it.split(',').map { Movement(it.first(), it.substring(1).toInt()) }
        .fold(mutableListOf(Point(0, 0)) as List<Point>) { path, movement -> path + movement.points(path.last()) }
}.requireNoNulls().let { (path1, path2) -> Pair(path1, path2) }

fun part1(input: Pair<List<Point>, List<Point>>) = input.let { (path1, path2) ->
    path1.intersect(path2).map { abs(it.x) + abs(it.y) }.filterNot { it == 0 }.min()
}

fun part2(input: Pair<List<Point>, List<Point>>) = input.let { (path1, path2) ->
    val otherPath = path2.toSet()
    path1.asSequence().withIndex().filter { it.value in otherPath }.map { intersection ->
        intersection.index + path2.indexOf(intersection.value)
    }.filterNot { it == 0 }.min()
}