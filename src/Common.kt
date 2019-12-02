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
    abstract fun execute(ip: Int, mem: MutableList<Int>): Int
    data class IndirectAddressed(val func: (Int, Int) -> Int) : IntCode() {
        override fun execute(ip: Int, mem: MutableList<Int>): Int {
            val dest = mem[ip + 3]
            val noun = mem[ip + 1]
            val verb = mem[ip + 2]
            mem[dest] = func(mem[noun], mem[verb])
            return ip + 4
        }
    }

    object End : IntCode() {
        override fun execute(ip: Int, mem: MutableList<Int>) = ip + 1
    }
}