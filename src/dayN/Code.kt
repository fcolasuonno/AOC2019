package dayN

import java.io.File

fun main() {
    val name = if (true) "test.txt" else "input.txt"
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

data class SomeObject(val i1: String)

private val lineStructure = """#(\d+) @ (\d+),(\d+): (\d+)x(\d+)""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure.matchEntire(it)?.destructured?.let {
        val (id) = it.toList()
        SomeObject(id)
    }
}.requireNoNulls()

fun part1(input: List<SomeObject>): Any? = input.size

fun part2(input: List<SomeObject>): Any? = input.size
