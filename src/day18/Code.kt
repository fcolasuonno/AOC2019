package day18

import isDebug
import java.io.File
import kotlin.math.abs

fun main() {
    val name = if (isDebug() && false) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val (parsed, walls) = parse(input)
    println(parsed to walls)
//    println("Part 1 = ${part1(parsed, walls)}")
//    println("Part 2 = ${part2(parsed, walls)}")
}

fun parse(input: List<String>) = input.withIndex().flatMap { (j, s) ->
    s.withIndex().mapNotNull { (i, c) ->
        when {
            c == '@' -> Tile.Pos
            c.isLowerCase() -> Tile.Key(c)
            c.isUpperCase() -> Tile.Door(c.toLowerCase())
            c == '#' -> Tile.Wall
            else -> null
        }?.let { Triple(i, j, it) }
    }
}.let {
    Step1(
        it.single { it.third == Tile.Pos }.let { it.first to it.second },
        it.filter { it.third is Tile.Key }.map { (it.first to it.second) to (it.third as Tile.Key) }.toMap(),
        it.filter { it.third is Tile.Door }.map { (it.first to it.second) to (it.third as Tile.Door) }.toMap()
    ) to it.filter { it.third == Tile.Wall }.map { (it.first to it.second) }.toSet()
}

fun part1(input: Step1, initialWalls: Set<Pair<Int, Int>>): Any? {
    val walls = initialWalls.toMutableSet()
    var size: Int
    do {
        size = walls.size
        val newWalls = walls.flatMap { it.neighbours() }.toSet()
            .filter { it !in walls && it !in input.doorMap && it !in input.keyMap && it.neighbours().count { it in walls } == 3 }
        walls.addAll(newWalls)
    } while (size != walls.size)
    val seen = mutableSetOf<String>().apply { add(input.hash()) }
    val frontier = input.neighbours(walls).toSortedSet(compareBy<Step1> {
        it.step
    }.thenBy {
        it.keyMap.size
    }.thenBy {
        it.pos.first
    }.thenBy {
        it.pos.second
    }.thenBy {
        it.keyMap.values.map { it.c }.sorted().joinToString("")
    })
    while (frontier.isNotEmpty()) {
        val first = frontier.first()
        if (first.keyMap.isEmpty()) {
            return first.step
        } else {
            frontier.remove(first)
            seen.add(first.hash())
            frontier.addAll(first.neighbours(walls).filter { it.hash() !in seen })
        }
    }
    return 0
}

fun part2(input: Step1, initialWalls: Set<Pair<Int, Int>>): Any? {
    val pos = input.pos
    val walls = (initialWalls + pos.neighbours() + pos).toMutableSet()
    var size: Int
    do {
        size = walls.size
        val newWalls = walls.flatMap { it.neighbours() }.toSet()
            .filter { (abs(it.first - pos.first) + abs(it.second - pos.second)) > 2 && it !in walls && it !in input.doorMap && it !in input.keyMap && it.neighbours().count { it in walls } == 3 }
        walls.addAll(newWalls)
    } while (size != walls.size)
    val others = input.keyMap.map { keyEntry ->
        val seen = mutableSetOf<Pair<Int, Int>>()
        val doors = input.doorMap.toMutableMap()
        val keys = input.keyMap.toMutableMap()
        val cost: MutableMap<Char, Pair<Int, String>> = mutableMapOf()
        val frontier = setOf(
            Step2(
                keyEntry.key,
                doors,
                keys,
                cost
            )
        ).toSortedSet(compareBy<Step2> { it.pos.first }.thenBy { it.pos.second })
        while (frontier.isNotEmpty()) {
            val first = frontier.first()
            frontier.remove(first)
            seen.add(first.pos)
            frontier.addAll(first.neighbours(walls).filter { it.pos !in seen && it.pos != keyEntry.key })
        }
        keyEntry.value.c to cost
    }.toMap()

    val seen = mutableSetOf<Pair<Int, Int>>()
    val doors = input.doorMap.toMutableMap()
    val keys = input.keyMap.toMutableMap()
    val cost: List<MutableMap<Char, Pair<Int, String>>> =
        listOf(mutableMapOf(), mutableMapOf(), mutableMapOf(), mutableMapOf())
    val frontier = mutableListOf(
        input.pos.copy(input.pos.first - 1, input.pos.second - 1),
        input.pos.copy(input.pos.first - 1, input.pos.second + 1),
        input.pos.copy(input.pos.first + 1, input.pos.second - 1),
        input.pos.copy(input.pos.first + 1, input.pos.second + 1)
    ).mapIndexed { index, pair ->
        Step2(pair, doors, keys, cost[index])
    }.toSortedSet(compareBy<Step2> { it.pos.first }.thenBy { it.pos.second })
    while (frontier.isNotEmpty()) {
        val first = frontier.first()
        frontier.remove(first)
        seen.add(first.pos)
        frontier.addAll(first.neighbours(walls).filter { it.pos !in seen })
    }
    val newFrontier = cost.mapIndexed { index, mutableMap ->
        mutableMap.filterValues { it.second.isEmpty() }.map { c ->
            Pair(List(4) { if (it == index) "${c.key}" else "" }, c.value.first)
        }
    }.flatten()
        .toSortedSet(compareBy<Pair<List<String>, Int>> { it.second }
            .thenBy { it.first.flatMap { it.toList() }.sorted().joinToString("") })
    while (newFrontier.isNotEmpty()) {
        val first = newFrontier.first()
        newFrontier.remove(first)
        if (first.first.sumBy { it.length } == (input.keyMap.toMutableMap().size)) {
            return first.second
        }
        val collectedKeys = first.first.joinToString("").toSet()
        val otherKeys = first.first.withIndex().filter { it.value.isNotEmpty() }.flatMap { (index, c) ->
            others.getValue(c.last()).filterValues { it.second.all { it in collectedKeys } }
                .filterKeys { it !in first.first.toString() }
                .map {
                    Pair(
                        first.first.mapIndexed { i, l -> if (i == index) (l + it.key) else l },
                        first.second + it.value.first
                    )
                }
        }
        val otherStarts = cost.withIndex().mapNotNull { (index, c) ->
            if (first.first[index].isNotEmpty()) null else
                c.filterKeys { it !in collectedKeys }
                    .filterValues { it.second.all { it in collectedKeys } }
                    .map {
                        Pair(
                            first.first.mapIndexed { i, l -> if (i == index) (l + it.key) else l },
                            first.second + it.value.first
                        )
                    }
        }.flatten()
        newFrontier.addAll(otherKeys + otherStarts)
    }
    return 0
}


sealed class Tile {
    object Wall : Tile()
    data class Key(val c: Char) : Tile()
    data class Door(val c: Char) : Tile()
    object Pos : Tile()
}

data class Step1(
    val pos: Pair<Int, Int>,
    val keyMap: Map<Pair<Int, Int>, Tile.Key>,
    val doorMap: Map<Pair<Int, Int>, Tile.Door>,
    val step: Int = 0
) {
    fun neighbours(walls: Set<Pair<Int, Int>>): List<Step1> =
        pos.neighbours().filter { it !in walls && it !in doorMap }.map {
            val key = keyMap[it]
            if (key == null) {
                copy(pos = it, step = step + 1)
            } else {
                copy(pos = it, keyMap = keyMap.filterValues { it != key }, doorMap = doorMap.filterValues {
                    it.c != key.c
                }, step = step + 1)
            }
        }

    fun hash() = "${pos.first},${pos.second},${keyMap.values.map { it.c }.sorted().joinToString("")}"
}

data class Step2(
    val pos: Pair<Int, Int>,
    val doors: MutableMap<Pair<Int, Int>, Tile.Door>,
    val keys: MutableMap<Pair<Int, Int>, Tile.Key>,
    val cost: MutableMap<Char, Pair<Int, String>>,
    var required: String = "",
    val step: Int = 0
) {
    fun neighbours(walls: Set<Pair<Int, Int>>): List<Step2> =
        pos.neighbours().filter { it !in walls }.map {
            var newRequired = required
            if (it in doors) {
                newRequired += doors.getValue(it).c
                doors.remove(it)
            }
            if (it in keys) {
                val key = keys.getValue(it)
                keys.remove(it)
                cost[key.c] = (step + 1) to newRequired
            }
            copy(pos = it, step = step + 1, required = newRequired)
        }

    override fun toString() = pos.toString()
}

private fun Pair<Int, Int>.neighbours() = listOf(
    copy(first = first - 1),
    copy(first = first + 1),
    copy(second = second - 1),
    copy(second = second + 1)
)
