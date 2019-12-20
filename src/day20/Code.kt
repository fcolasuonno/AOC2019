package day20

import isDebug
import java.io.File
import kotlin.math.abs

fun main() {
    val name = if (isDebug()) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val (paths, portals) = parse(input)
    println("Part 1 = ${part1(paths, portals)}")
    println("Part 2 = ${part2(paths, portals)}")
}

fun parse(input: List<String>) = input.withIndex().flatMap { (j, s) ->
    s.withIndex().mapNotNull { (i, c) ->
        if (c == '.') (i to j) else null
    }
}.toSet().let { passages ->
    Pair(passages, input.withIndex().flatMap { (j, s) ->
        s.withIndex().mapNotNull { (i, c) ->
            if (c.isLetter()) ((i to j) to c) else null
        }
    }.toMap().let { portal ->
        portal.filterKeys { it.neighbours().any { it in passages } }.mapValues {
            (it.key.neighbours().take(2) + it.key + it.key.neighbours().drop(2)).mapNotNull { portal[it] }
                .joinToString("")
        }
    })
}

fun part1(input: Set<Pair<Int, Int>>, portals: Map<Pair<Int, Int>, String>): Any? {
    val start = portals.filterValues { it == "AA" }.flatMap { it.key.neighbours() }.single { it in input }
    val end = portals.filterValues { it == "ZZ" }.flatMap { it.key.neighbours() }.single { it in input }
    val wormholePos = portals.entries.groupBy { it.value }.filter { it.value.size == 2 }
        .map { it.value.map { it.key.neighbours().single { it in input } } }.flatMap { (fromPos, toPos) ->
            listOf(fromPos to toPos, toPos to fromPos)
        }.toMap()
    val seen = mutableSetOf<Pair<Int, Int>>()
    val frontier = listOf(start).map { it to 0 }
        .toSortedSet(compareBy<Pair<Pair<Int, Int>, Int>> { it.second }.thenBy { it.first.first }.thenBy { it.first.second })
    while (frontier.isNotEmpty()) {
        val pos = frontier.first()
        frontier.remove(pos)
        seen.add(pos.first)
        if (pos.first == end) {
            return pos.second
        }
        val next = pos.first.neighbours().filter { it in input } + wormholePos[pos.first]
        frontier.addAll(next.filterNotNull().filter { it !in seen }.map { it to (pos.second + 1) })
    }
    return 0
}

fun part2(input: Set<Pair<Int, Int>>, portals: Map<Pair<Int, Int>, String>): Any? {
    val start = portals.filterValues { it == "AA" }.flatMap { it.key.neighbours() }.single { it in input }
    val end = portals.filterValues { it == "ZZ" }.flatMap { it.key.neighbours() }.single { it in input }
    val wormholePos = portals.entries.groupBy { it.value }.filter { it.value.size == 2 }
        .map { it.value.map { it.key.neighbours().single { it in input } } }.flatMap { (fromPos, toPos) ->
            listOf(fromPos to toPos, toPos to fromPos)
        }.toMap()
    val innerWormholePos = wormholePos.filterKeys { !it.isOuter(input) }
    val outerWormholePos = wormholePos.filterKeys { it.isOuter(input) }
    val seen = mutableSetOf<Triple<Int, Int, Int>>()
    val frontier = listOf(start).map { Triple(it, 0, 0) }
        .toSortedSet(compareBy<Triple<Pair<Int, Int>, Int, Int>> { it.third }.thenBy { abs(it.second) }.thenBy { it.first.first }.thenBy { it.first.second })
    while (frontier.isNotEmpty()) {
        val pos = frontier.first()
        frontier.remove(pos)
        seen.add(Triple(pos.first.first, pos.first.second, pos.second))
        if (pos.first == end && pos.second == 0) {
            return pos.third
        }
        val next = pos.first.neighbours().filter { it in input }.map { Triple(it.first, it.second, pos.second) } +
                innerWormholePos[pos.first]?.let { Triple(it.first, it.second, pos.second + 1) } +
                outerWormholePos[pos.first]?.takeIf { pos.second != 0 }?.let {
                    Triple(
                        it.first,
                        it.second,
                        pos.second - 1
                    )
                }
        frontier.addAll(next.filterNotNull().filter { it !in seen }.map {
            Triple(
                it.first to it.second,
                it.third,
                pos.third + 1
            )
        })
    }
    return 0
}

private fun Pair<Int, Int>.isOuter(input: Set<Pair<Int, Int>>) =
    first == 2 || second == 2 || first == input.map { it.first }.max() || second == input.map { it.second }.max()

private fun Pair<Int, Int>.neighbours() = listOf(
    copy(first = first - 1),
    copy(second = second - 1),
    copy(first = first + 1),
    copy(second = second + 1)
)