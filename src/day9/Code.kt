package day9

import IntCode
import isDebug
import java.io.File

fun main() {
    val name = if (isDebug() && false) "test.txt" else "input.txt"
    System.err.println(name)
    val dir = ::main::class.java.`package`.name
    val input = File("src/$dir/$name").readLines()
    val parsed = parse(input)
//    println("Part 1 = ${part1(parsed)}")
    println("Part 2 = ${part2(parsed)}")
}

fun parse(input: List<String>) = input.map {
    it.split(",").map { it.toLong() }
}.requireNoNulls().single()

val mainOpcodes = mapOf(
    1L to IntCode.Compute { a, b -> a + b },
    2L to IntCode.Compute { a, b -> a * b },
    5L to IntCode.Jump { a -> a != 0L },
    6L to IntCode.Jump { a -> a == 0L },
    7L to IntCode.Compare { a, b -> a < b },
    8L to IntCode.Compare { a, b -> a == b },
    9L to IntCode.SetBase,
    99L to IntCode.End
)

fun part1(input: List<Long>): Any? {
    val inputs = mutableListOf<Long>(1L)
    val output = mutableListOf<Long>()
    val opcodes = mainOpcodes + mapOf(
        3L to IntCode.Input {
            inputs.removeAt(0)
        },
        4L to IntCode.Output { a ->
            output.add(a)
        })
    val mem = input.mapIndexed { index, i -> index.toLong() to i }.toMap().toMutableMap()
    generateSequence(0L) { ip ->
        System.err.println(
            "ip= $ip ${(mem[ip] ?: 0)},${(mem[ip + 1] ?: 0)},${(mem[ip + 2] ?: 0)},${(mem[ip + 3]
                ?: 0)} ${opcodes[(mem[ip] ?: 0) % 100]?.javaClass?.simpleName}"
        )
        opcodes[(mem[ip] ?: 0) % 100]?.execute(ip, mem, (mem[ip] ?: 0) / 100)
    }.first {
        opcodes[mem[it]] == IntCode.End
    }
    return output
}

fun part2(input: List<Long>): Any? {
    val inputs = mutableListOf<Long>(2L)
    val output = mutableListOf<Long>()
    val opcodes = mainOpcodes + mapOf(
        3L to IntCode.Input {
            inputs.removeAt(0)
        },
        4L to IntCode.Output { a ->
            output.add(a)
        })
    val mem = input.mapIndexed { index, i -> index.toLong() to i }.toMap().toMutableMap()
    generateSequence(0L) { ip ->
        System.err.println(
            "ip= $ip ${(mem[ip] ?: 0)},${(mem[ip + 1] ?: 0)},${(mem[ip + 2] ?: 0)},${(mem[ip + 3]
                ?: 0)} ${opcodes[(mem[ip] ?: 0) % 100]?.javaClass?.simpleName}"
        )
        opcodes[(mem[ip] ?: 0) % 100]?.execute(ip, mem, (mem[ip] ?: 0) / 100)
    }.first {
        opcodes[mem[it]] == IntCode.End
    }
    return output
}
