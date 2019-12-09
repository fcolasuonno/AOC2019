import java.lang.management.ManagementFactory


fun <E> List<MutableList<E>>.printWith(byLines: Boolean = true, function: (E) -> String) = buildString {
    append('\n')
    if (byLines) {
        for (y in 0 until this@printWith[0].size) {
            for (x in 0 until this@printWith.size) {
                append(function(this@printWith[x][y]))
            }
            append('\n')
        }
    } else {
        for (y in 0 until this@printWith.size) {
            for (x in 0 until this@printWith[0].size) {
                append(function(this@printWith[y][x]))
            }
            append('\n')
        }
    }
}

val <E> Set<E>.permutations: List<List<E>>
    get() = if (size == 1) listOf(listOf(first())) else flatMap { element ->
        minus(element).permutations.map { it + element }
    }

val <E> Set<E>.combinations: Set<Set<E>>
    get() = when (size) {
        0 -> setOf(emptySet())
        1 -> setOf(emptySet(), setOf(first()))
        else -> setOf(emptySet<E>()) + map { element -> setOf(element) } + flatMap { element -> minus(element).combinations.map { it + element } }
    }

class MultiMap<K1, K2, V> : HashMap<K1, MultiMap.ValueMap<K2, V>>(), MutableMap<K1, MultiMap.ValueMap<K2, V>> {
    class ValueMap<K2, V> : HashMap<K2, V>(), MutableMap<K2, V> {
        override fun get(key: K2) = super.get(key) ?: throw IllegalAccessError()
    }

    override fun get(key: K1) = super.get(key) ?: ValueMap<K2, V>().also { put(key, it) }
}

fun isDebug() = ManagementFactory.getRuntimeMXBean().inputArguments.any { "jdwp=" in it }

data class IntCodeComputer(
    val program: List<Long>,
    val input: IntCode.Input? = null,
    val output: IntCode.Output? = null,
    val startingIp: Long = 0,
    val id: Int = 0
) {
    private val opcodes = mapOf(
        1L to IntCode.Compute(Long::plus),
        2L to IntCode.Compute(Long::times),
        3L to input,
        4L to output,
        5L to IntCode.Jump { a -> a != 0L },
        6L to IntCode.Jump { a -> a == 0L },
        7L to IntCode.Compare { a, b -> a < b },
        8L to IntCode.Compare(Long::equals),
        9L to IntCode.SetBase { a -> relativeBase += a },
        99L to IntCode.End
    )
    var relativeBase = 0L
    val mem = program.mapIndexed { index, i -> index.toLong() to i }.toMap().toMutableMap()
    private fun op(ip: Long) = opcodes[mem[ip]?.rem(100) ?: 0]
    var peekOp: IntCode? = op(0)
    val sequence = generateSequence(startingIp) { ip ->
        val intCode = op(ip)
//        System.err.println("${id} ip= $ip ${(ip..(ip + 3)).map { mem[it] }} ${intCode?.javaClass?.simpleName}")
        intCode?.execute(ip, mem, relativeBase).also {
            peekOp = it?.let { op(it) }
        }
    }

    fun run() = sequence.first { opcodes[mem[it]] == IntCode.End }
}

typealias Memory = MutableMap<Long, Long>

sealed class IntCode {
    abstract fun execute(
        ip: Long,
        mem: Memory,
        relativeBase: Long = 0,
        modes: List<Char> = mem[ip]?.div(100)?.toString()?.toList().orEmpty()
    ): Long

    protected fun access(ip: Long, mem: Memory, mode: List<Char>, relativeBase: Long, position: Int) =
        when (mode.getOrNull(mode.size - position) ?: '0') {
            '0' -> mem[ip + position] ?: 0
            '1' -> ip + position
            '2' -> (mem[ip + position] ?: 0) + relativeBase
            else -> throw IllegalAccessError()
        }

    data class Compute(val func: (Long, Long) -> Long) : IntCode() {
        override fun execute(ip: Long, mem: Memory, relativeBase: Long, modes: List<Char>): Long {
            val noun = mem[access(ip, mem, modes, relativeBase, 1)] ?: 0
            val verb = mem[access(ip, mem, modes, relativeBase, 2)] ?: 0
            mem[access(ip, mem, modes, relativeBase, 3)] = func(noun, verb)
            return ip + 4
        }
    }

    data class Input(val func: () -> Long) : IntCode() {
        override fun execute(ip: Long, mem: Memory, relativeBase: Long, modes: List<Char>): Long {
            mem[access(ip, mem, modes, relativeBase, 1)] = func()
            return ip + 2
        }
    }

    data class Output(val func: (Long) -> Unit) : IntCode() {
        override fun execute(ip: Long, mem: Memory, relativeBase: Long, modes: List<Char>): Long {
            val noun = mem[access(ip, mem, modes, relativeBase, 1)] ?: 0
            func(noun)
            return ip + 2
        }
    }

    data class Jump(val func: (Long) -> Boolean) : IntCode() {
        override fun execute(ip: Long, mem: Memory, relativeBase: Long, modes: List<Char>): Long {
            val noun = mem[access(ip, mem, modes, relativeBase, 1)] ?: 0
            val verb = mem[access(ip, mem, modes, relativeBase, 2)] ?: 0
            return if (func(noun)) verb else (ip + 3)
        }
    }

    data class Compare(val func: (Long, Long) -> Boolean) : IntCode() {
        override fun execute(ip: Long, mem: Memory, relativeBase: Long, modes: List<Char>): Long {
            val noun = mem[access(ip, mem, modes, relativeBase, 1)] ?: 0
            val verb = mem[access(ip, mem, modes, relativeBase, 2)] ?: 0
            mem[access(ip, mem, modes, relativeBase, 3)] = if (func(noun, verb)) 1L else 0L
            return ip + 4
        }
    }

    data class SetBase(val func: (Long) -> Unit) : IntCode() {
        override fun execute(ip: Long, mem: Memory, relativeBase: Long, modes: List<Char>): Long {
            val noun = mem[access(ip, mem, modes, relativeBase, 1)] ?: 0
            func(noun)
            return ip + 2
        }
    }

    object End : IntCode() {
        override fun execute(ip: Long, mem: Memory, relativeBase: Long, modes: List<Char>) = ip + 1
    }
}

private fun Long.pow(exponent: Long): Long = (0 until exponent).fold(1L) { a, _ -> a * this }