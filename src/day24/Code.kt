package day24

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

fun parse(input: List<String>) = input.withIndex().flatMap { (j, s) ->
    s.withIndex().mapNotNull { (i, c) -> if (c == '#') i to j else null }
}.toSet()

fun part1(input: Set<Pair<Int, Int>>) = mutableSetOf<Set<Pair<Int, Int>>>().let { seen ->
    generateSequence(input) { prev ->
        (prev.flatMap { it.neighbours() }.filter { it !in prev && it.neighbours().count { it in prev } in 1..2 } +
                prev.filter { it.neighbours().count { it in prev } == 1 }).toSet()
    }.first { state ->
        (state in seen).also { seen.add(state) }
    }.sumBy { 1 shl (it.second * 5 + it.first) }
}


fun part2(initialInput: Set<Pair<Int, Int>>) =
    initialInput.map { Triple(it.first, it.second, 0) }.toSet().let { input ->
        generateSequence(input) { prev ->
            (prev.flatMap { it.neighbours() }.filter { it !in prev && it.neighbours().count { it in prev } in 1..2 } +
                    prev.filter { it.neighbours().count { it in prev } == 1 }).toSet()
        }.take(1 + 200).last().size
    }

private fun Triple<Int, Int, Int>.neighbours() = when {
    first == 2 && second == 1 -> listOf(
        copy(first = first - 1),
        copy(second = second - 1),
        copy(first = first + 1),
        copy(first = 0, second = 0, third = third + 1),
        copy(first = 1, second = 0, third = third + 1),
        copy(first = 2, second = 0, third = third + 1),
        copy(first = 3, second = 0, third = third + 1),
        copy(first = 4, second = 0, third = third + 1)
    )
    first == 2 && second == 3 -> listOf(
        copy(first = first - 1),
        copy(first = first + 1),
        copy(second = second + 1),
        copy(first = 0, second = 4, third = third + 1),
        copy(first = 1, second = 4, third = third + 1),
        copy(first = 2, second = 4, third = third + 1),
        copy(first = 3, second = 4, third = third + 1),
        copy(first = 4, second = 4, third = third + 1)
    )
    first == 1 && second == 2 -> listOf(
        copy(first = first - 1),
        copy(second = second - 1),
        copy(second = second + 1),
        copy(first = 0, second = 0, third = third + 1),
        copy(first = 0, second = 1, third = third + 1),
        copy(first = 0, second = 2, third = third + 1),
        copy(first = 0, second = 3, third = third + 1),
        copy(first = 0, second = 4, third = third + 1)
    )
    first == 3 && second == 2 -> listOf(
        copy(second = second - 1),
        copy(first = first + 1),
        copy(second = second + 1),
        copy(first = 4, second = 0, third = third + 1),
        copy(first = 4, second = 1, third = third + 1),
        copy(first = 4, second = 2, third = third + 1),
        copy(first = 4, second = 3, third = third + 1),
        copy(first = 4, second = 4, third = third + 1)
    )
    else -> listOf(
        if (first == 0) copy(first = 1, second = 2, third = third - 1) else copy(first = first - 1),
        if (second == 0) copy(first = 2, second = 1, third = third - 1) else copy(second = second - 1),
        if (first == 4) copy(first = 3, second = 2, third = third - 1) else copy(first = first + 1),
        if (second == 4) copy(first = 2, second = 3, third = third - 1) else copy(second = second + 1)
    )
}

private fun Pair<Int, Int>.neighbours() = listOf(
    copy(first = first - 1),
    copy(second = second - 1),
    copy(first = first + 1),
    copy(second = second + 1)
).filter { it.first in 0 until 5 && it.second in 0 until 5 }