package day12

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

data class Moon(
    val x: Int, val y: Int, val z: Int,
    val vx: Int = 0, val vy: Int = 0, val vz: Int = 0
) {
    fun updateVelocity(set: List<Moon>) = copy(
        vx = vx - set.count { it.x < x } + set.count { it.x > x },
        vy = vy - set.count { it.y < y } + set.count { it.y > y },
        vz = vz - set.count { it.z < z } + set.count { it.z > z })

    fun updatePosition() = copy(x = x + vx, y = y + vy, z = z + vz)
}

private val lineStructure = """<x=(-?\d+), y=(-?\d+), z=(-?\d+)>""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (x, y, z) = it.toList().map { it.toInt() }
        Moon(x, y, z)
    }
}.requireNoNulls()

fun part1(input: List<Moon>, steps: Int = 1000) = generateSequence(input) { moons ->
    moons.map {
        it.updateVelocity(moons - it)
    }.map {
        it.updatePosition()
    }
}.take(steps + 1).last().sumBy {
    (abs(it.x) + abs(it.y) + abs(it.z)) *
            (abs(it.vx) + abs(it.vy) + abs(it.vz))
}

fun part2(input: List<Moon>) = generateSequence(input) { moons ->
    moons.map {
        it.updateVelocity(moons - it)
    }.map {
        it.updatePosition()
    }
}.drop(1).let { sequence ->
    lcm(
        1L + sequence.indexOfFirst { it.all { it.vx == 0 } && it.map { it.x } == input.map { it.x } },
        lcm(1L + sequence.indexOfFirst { it.all { it.vy == 0 } && it.map { it.y } == input.map { it.y } },
            1L + sequence.indexOfFirst { it.all { it.vz == 0 } && it.map { it.z } == input.map { it.z } }
        )
    )
}

fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)
fun lcm(a: Long, b: Long): Long = a / gcd(a, b) * b