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

sealed class IntCode {
    companion object {
        var relativeBase = 0L
    }

    abstract fun execute(ip: Long, mem: MutableMap<Long, Long>, mode: Long = 0): Long
    fun indirect(mode: Long, position: Long) = mode / (10L.pow(position - 1)) % 10 == 0L
    fun relative(mode: Long, position: Long) = mode / (10L.pow(position - 1)) % 10 == 2L

    protected fun access(mem: Map<Long, Long>, ip: Long, position: Long, mode: Long) =
        when {
            indirect(mode, position) -> mem[mem[ip + position] ?: 0]
            relative(mode, position) -> mem[(mem[ip + position] ?: 0) + relativeBase]
            else -> mem[ip + position]
        } ?: 0

    data class Compute(val func: (Long, Long) -> Long) : IntCode() {
        override fun execute(ip: Long, mem: MutableMap<Long, Long>, mode: Long): Long {
            val noun = access(mem, ip, 1, mode)
            val verb = access(mem, ip, 2, mode)
            when {
                indirect(mode, 3) -> mem[mem[ip + 3] ?: 0] = func(noun, verb)
                relative(mode, 3) -> mem[(mem[ip + 3] ?: 0) + relativeBase] = func(noun, verb)
                else -> mem[ip + 3] = func(noun, verb)
            }
            return ip + 4
        }
    }

    data class Input(val func: () -> Long) : IntCode() {
        override fun execute(ip: Long, mem: MutableMap<Long, Long>, mode: Long): Long {
            when {
                indirect(mode, 1) -> mem[mem[ip + 1] ?: 0] = func()
                relative(mode, 1) -> mem[(mem[ip + 1] ?: 0) + relativeBase] = func()
                else -> mem[ip + 1] = func()
            }
            return ip + 2
        }
    }

    data class Output(val func: (Long) -> Unit) : IntCode() {
        override fun execute(ip: Long, mem: MutableMap<Long, Long>, mode: Long): Long {
            val noun = access(mem, ip, 1, mode)
            func(noun)
            return ip + 2
        }
    }

    data class Jump(val func: (Long) -> Boolean) : IntCode() {
        override fun execute(ip: Long, mem: MutableMap<Long, Long>, mode: Long): Long {
            val noun = access(mem, ip, 1, mode)
            val verb = access(mem, ip, 2, mode)
            return if (func(noun)) verb else (ip + 3)
        }
    }

    data class Compare(val func: (Long, Long) -> Boolean) : IntCode() {
        override fun execute(ip: Long, mem: MutableMap<Long, Long>, mode: Long): Long {
            val noun = access(mem, ip, 1, mode)
            val verb = access(mem, ip, 2, mode)
            when {
                indirect(mode, 3) -> mem[mem[ip + 3] ?: 0] = if (func(noun, verb)) 1L else 0L
                relative(mode, 3) -> mem[(mem[ip + 3] ?: 0) + relativeBase] = if (func(noun, verb)) 1L else 0L
                else -> mem[ip + 3] = if (func(noun, verb)) 1L else 0L
            }
            return ip + 4
        }
    }

    object SetBase : IntCode() {
        override fun execute(ip: Long, mem: MutableMap<Long, Long>, mode: Long): Long {
            val noun = access(mem, ip, 1, mode)
            relativeBase += noun
            return ip + 2
        }
    }

    object End : IntCode() {
        override fun execute(ip: Long, mem: MutableMap<Long, Long>, mode: Long) = ip + 1
    }
}

private fun Long.pow(exponent: Long): Long = (0 until exponent).fold(1L) { a, _ -> a * this }