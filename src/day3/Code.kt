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
        }.also { p -> p.step = point.step + it }
    }
}

data class Point(val x: Int, val y: Int) {
    var step = 0
}

fun parse(input: List<String>) = input.map {
    it.split(',').map { Movement(it.first(), it.substring(1).toInt()) }
}.requireNoNulls()

fun part1(input: List<List<Movement>>) = input.map {
    var point = Point(0, 0)
    it.flatMap { movement ->
        movement.points(point).also {
            point = it.last()
        }
    }
}.let { (path1, path2) ->
    path1.intersect(path2).map { abs(it.x) + abs(it.y) }.min()
}

fun part2(input: List<List<Movement>>) = input.map {
    var point = Point(0, 0)
    it.flatMap { movement ->
        movement.points(point).also {
            point = it.last()
        }
    }
}.let { (path1, path2) ->
    val seen = path2.toSet()
    path1.filter { it in seen }.map { p -> p.step + path2.first { it == p }.step }.min()
}