package day22

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

interface Shuffle {
    fun shuffle(cards: List<Int>): List<Int>
    fun shuffleSingle(card: Long, cardSize: Long): Long
}

object Reverse : Shuffle {
    override fun shuffle(cards: List<Int>) = cards.reversed()
    override fun shuffleSingle(card: Long, cardSize: Long) = cardSize - 1 - card
}

data class Cut(val n: Int) : Shuffle {
    override fun shuffle(cards: List<Int>) =
        cards.slice(cards.indices.let { it.drop((cards.size + n) % cards.size) + it.take((cards.size + n) % cards.size) })

    override fun shuffleSingle(card: Long, cardSize: Long): Long {
        val point = (cardSize + n) % cardSize
        return if (card < point) {
            card + cardSize - point
        } else {
            card - point
        }
    }
}

data class Increment(val n: Int) : Shuffle {
    override fun shuffle(cards: List<Int>): List<Int> {
        val l = cards.indices.toMutableList()
        cards.indices.forEachIndexed { index, i ->
            l[(index * n) % cards.size] = i
        }
        return cards.slice(l)
    }

    override fun shuffleSingle(card: Long, cardSize: Long): Long = card * n % cardSize
}

private val lineStructure1 = """deal into new stack""".toRegex()
private val lineStructure2 = """cut (-?\d+)""".toRegex()
private val lineStructure3 = """deal with increment (\d+)""".toRegex()

fun parse(input: List<String>) = input.map {
    lineStructure1.matchEntire(it)?.let { Reverse }
        ?: lineStructure2.matchEntire(it)?.destructured?.let { Cut(it.component1().toInt()) }
        ?: lineStructure3.matchEntire(it)?.destructured?.let { Increment(it.component1().toInt()) }
}.requireNoNulls()

fun part1(input: List<Shuffle>, cards: List<Int> = (0..10006).toList()) =
    input.fold(cards) { c, op -> op.shuffle(c) }.indexOf(2019)

fun part2(input: List<Shuffle>) = input.fold(2020L) { c, op -> op.shuffleSingle(c, 119315717514047L) }