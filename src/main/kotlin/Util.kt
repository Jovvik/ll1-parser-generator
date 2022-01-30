fun <K, V> MutableMap<K, MutableSet<V>>.getOrPutEmpty(x: K): MutableSet<V> {
    return this.getOrPut(x, ::mutableSetOf)
}

fun <R> Set<R>.intersects(s: Set<R>) = this.any { s.contains(it) }

sealed interface Rule {
    val name: String
}

data class NonTerminalRule(
    override val name: String,
    val arguments: List<Argument>,
    val productions: List<Production>,
    val returnType: String?
) : Rule

open class RegexRule(
    override val name: String,
    regexString: String
) : Rule {
    val regex = regexString.dropFirstLast().replace("\\", "\\\\")

    override fun toString(): String {
        return "TerminalRule(name='$name', regex=$regex)"
    }
}

class SkipRule(
    name: String,
    regexString: String
) : RegexRule(name, regexString)

class TerminalRule(
    name: String,
    regexString: String
) : RegexRule(name, regexString)

class Atom(val name: String, val arguments: List<String>, code: String?) {
    val code = code?.dropFirstLast()
}

class Production(val atoms: List<Atom>, finalCode: String?) {
    val finalCode = finalCode?.dropFirstLast()
}

data class Argument(val name: String, val type: String)

fun String.dropFirstLast() = drop(1).dropLast(1)