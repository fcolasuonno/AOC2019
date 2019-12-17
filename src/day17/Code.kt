package day17

import IntCode
import IntCodeComputer
import isDebug
import java.io.File

private fun Pair<Int, Int>.neighbours() = listOf(
    copy(first = first - 1),
    copy(first = first + 1),
    copy(second = second - 1),
    copy(second = second + 1)
)

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

fun part1(input: List<Long>) = mutableListOf<Long>().apply {
    IntCodeComputer(input, null, IntCode.Output { a -> add(a) }).run()
}.map { it.toChar() }.joinToString("").split("\n")
    .withIndex().flatMap { (j, s) ->
        s.withIndex().mapNotNull { (i, c) -> (i to j).takeIf { c != '.' } }
    }.toSet()
    .run {
        filter { it.neighbours().all { it in this } }.sumBy { it.first * it.second }
    }

data class Robot(val position: Pair<Int, Int>, val direction: Direction, val step: Int, val turn: Direction) {

    private operator fun Pair<Int, Int>.plus(dir: Direction) = copy(first = first + dir.dx, second = second + dir.dy)
    fun straight(map: Set<Pair<Int, Int>>) =
        (position + direction).takeIf { it in map }?.let { copy(position = it, step = step + 1) }

    fun left(map: Set<Pair<Int, Int>>) = when (direction) {
        Direction.U -> Direction.L
        Direction.D -> Direction.R
        Direction.L -> Direction.D
        Direction.R -> Direction.U
    }.let { Pair(it, position + it) }.takeIf { it.second in map }?.let {
        copy(
            position = it.second,
            direction = it.first,
            step = 1,
            turn = Direction.L
        )
    }

    fun right(map: Set<Pair<Int, Int>>) = when (direction) {
        Direction.U -> Direction.R
        Direction.D -> Direction.L
        Direction.L -> Direction.U
        Direction.R -> Direction.D
    }.let { Pair(it, position + it) }.takeIf { it.second in map }?.let {
        copy(
            position = it.second,
            direction = it.first,
            step = 1,
            turn = Direction.R
        )
    }

    override fun toString() = "$turn,$step"
}

enum class Direction(val dx: Int, val dy: Int) {
    U(0, -1),
    D(0, 1),
    L(-1, 0),
    R(1, 0);
}

fun part2(codeInput: List<Long>): Any? {
    val output = mutableListOf<Long>()
    val input = mutableListOf<Long>()
    val computer = IntCodeComputer(codeInput, IntCode.Input {
        input.removeAt(0)
    }, IntCode.Output { a ->
        output.add(a)
    }).apply {
        mem[0] = 2L
    }
    computer.runWhile { peekOp is IntCode.Input }

    val (map, initialRobot) = output.map { it.toChar() }.joinToString("").split("\n")
        .withIndex().flatMap { (j, s) -> s.mapIndexed { i, c -> (i to j) to c } }.let {
            it.filter { it.second != '.' }.map { it.first }.toSet() to it.single { it.second == '^' }.first
        }

    val fullDirections = generateSequence(Robot(initialRobot, Direction.L, 0, Direction.L)) { robot ->
        robot.straight(map) ?: robot.left(map) ?: robot.right(map)
    }.let { it.zipWithNext().filter { it.second.direction != it.first.direction }.map { it.first } + it.last() }
        .joinToString(",", postfix = ",")

    val compressedDirection =
        (21 downTo 2).flatMap { a -> (21 downTo 2).flatMap { b -> (21 downTo 2).map { c -> Triple(a, b, c) } } }
            .first { (a, b, c) ->
                val aMovement = fullDirections.take(a)
                val aReplaced = fullDirections.replace(aMovement, "")
                val bMovement = aReplaced.take(b)
                val bReplaced = aReplaced.replace(bMovement, "")
                bReplaced.replace(bReplaced.take(c), "").isEmpty()
            }.let { (a, b, c) ->
                val aMovement = fullDirections.take(a)
                val aReplaced = fullDirections.replace(aMovement, "")
                val bMovement = aReplaced.take(b)
                val bReplaced = aReplaced.replace(bMovement, "")
                val cMovement = bReplaced.take(c)
                listOf(
                    fullDirections.replace(aMovement, "A,").replace(bMovement, "B,").replace(cMovement, "C,"),
                    aMovement,
                    bMovement,
                    cMovement
                ).joinToString("\n", postfix = "\nn\n") { it.dropLast(1) }
            }
    input.addAll(compressedDirection.map { it.toLong() })
    computer.run()
    return output.last()
}
