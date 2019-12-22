package day22

import isDebug
import java.io.File
import java.math.BigInteger

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
    fun apply(offsetIncrement: Pair<BigInteger, BigInteger>, deckSize: BigInteger): Pair<BigInteger, BigInteger>
}

object Reverse : Shuffle {
    override fun shuffle(cards: List<Int>) = cards.reversed()
    override fun apply(
        offsetIncrement: Pair<BigInteger, BigInteger>,
        deckSize: BigInteger
    ): Pair<BigInteger, BigInteger> {
        val (offset, increment) = offsetIncrement
        val newIncrement = (increment * (-1).toBigInteger()).mod(deckSize)
        val newOffset = (offset + newIncrement).mod(deckSize)
        return newOffset to newIncrement
    }
}

data class Cut(val n: Int) : Shuffle {
    override fun shuffle(cards: List<Int>) =
        cards.slice(cards.indices.let { it.drop((cards.size + n) % cards.size) + it.take((cards.size + n) % cards.size) })

    override fun apply(
        offsetIncrement: Pair<BigInteger, BigInteger>,
        deckSize: BigInteger
    ): Pair<BigInteger, BigInteger> {
        val (offset, increment) = offsetIncrement
        val newOffset = (offset + (increment * n.toBigInteger())).mod(deckSize)
        return newOffset to increment
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

    override fun apply(
        offsetIncrement: Pair<BigInteger, BigInteger>,
        deckSize: BigInteger
    ): Pair<BigInteger, BigInteger> {
        val (offset, increment) = offsetIncrement
        val newIncrement = (increment * n.toBigInteger().modInverse(deckSize)).mod(deckSize)
        return offset to newIncrement
    }
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

fun part2(input: List<Shuffle>): BigInteger {
    //CHEATED THIS PART.. would never have found it
    val deckSize = 119315717514047L.toBigInteger()
    val iterations = 101741582076661L.toBigInteger()
    val (offset, increment) = input.fold(BigInteger.ZERO to BigInteger.ONE) { c, op -> op.apply(c, deckSize) }
    val finalIncrement = increment.modPow(iterations, deckSize)
    val finalOffset =
        (offset * (BigInteger.ONE - increment.modPow(iterations, deckSize)) * (BigInteger.ONE - increment).modInverse(
            deckSize
        )).mod(deckSize)
    return (finalOffset + (finalIncrement * 2020.toBigInteger())).mod(deckSize)
}