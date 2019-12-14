package day14

import isDebug
import java.io.File

fun main() {
    val name = if (isDebug()) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val (parsed, ranks) = parse(input)
    println("Part 1 = ${part1(parsed, ranks)}")
    println("Part 2 = ${part2(parsed, ranks)}")
}

data class Required(val amount: Long, val source: String) {
    operator fun times(requiredAmount: Long) = this.copy(amount = amount * requiredAmount)
}

data class Production(val amount: Int, val chems: List<Required>) {
    fun expand(input: List<Required>) =
        chems.map { it * ((amount + input.first().amount - 1) / amount) } + input.drop(1)
}

fun parse(input: List<String>) = input.map {
    val (source, product) = it.split(" => ")
    val (prodAmount, prodName) = product.split(" ")
    prodName to Production(prodAmount.toInt(), source.split(", ").map {
        val (srcAmount, srcName) = it.split(" ")
        Required(srcAmount.toLong(), srcName)
    })
}.requireNoNulls().toMap().let {
    it to mutableMapOf("ORE" to 0).apply {
        val inputCopy = it.toMutableMap()
        while (inputCopy.isNotEmpty()) {
            val (key, value) = inputCopy.entries.first { it.value.chems.all { it.source in this } }
            inputCopy.remove(key)
            this[key] = value.chems.map { getValue(it.source) }.max()!! + 1
        }
    }
}

fun part1(input: Map<String, Production>, ranks: MutableMap<String, Int>, requiredAmount: Long = 1L) =
    generateSequence(listOf(Required(requiredAmount, "FUEL"))) {
        it.takeUnless { it.size == 1 && it.single().source == "ORE" }
            ?.sortedByDescending { ranks[it.source] }?.let {
                input.getValue(it.first().source).expand(it).groupBy { it.source }
                    .values.map { Required(it.map { it.amount }.sum(), it.first().source) }
            }
    }.last().single().amount

fun part2(input: Map<String, Production>, ranks: MutableMap<String, Int>): Any? {
    var min = 0L
    var max = 1000000000000L
    while (min <= max) {
        val middle = (min + max) / 2L
        part1(input, ranks, middle).let {
            if (it < 1000000000000L) {
                min = middle + 1
            } else
                max = middle - 1
        }
    }
    return min - 1
}