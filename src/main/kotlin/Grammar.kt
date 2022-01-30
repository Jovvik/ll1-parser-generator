class Grammar(
    val name: String,
    rules: List<Rule>,
    val startNonTerminal: String,
    headerCode: String?,
    fieldsCode: String?
) {
    companion object {
        private const val EPS = "_EPS"
        private const val END = "END"
    }

    val header = headerCode?.dropFirstLast()
    val fields = fieldsCode?.dropFirstLast()
    val skipRules = rules.filterIsInstance<SkipRule>()
    val terminalRules = rules.filterIsInstance<TerminalRule>()
    val nonTerminalRules = rules.filterIsInstance<NonTerminalRule>()
    val startRule: NonTerminalRule = nonTerminalRules.firstOrNull { it.name == startNonTerminal }
        ?: throw GrammarException("No rule for starting non-terminal")
    private val first = mutableMapOf<String, MutableSet<String>>()
    private val follow = mutableMapOf<String, MutableSet<String>>()
    private val terminals = terminalRules.map { it.name }.toSet()

    init {
        checkDuplicateRules()
        checkUndefinedAtoms()
        checkArgumentLengths()
        if (startRule.arguments.isNotEmpty()) {
            throw GrammarException("Start rule should not have any arguments")
        }
        computeFirst()
        if (first[startRule.name].isNullOrEmpty()) {
            throw GrammarException("Nothing can be derived from the start rule")
        }
        println(first)
        computeFollow()
        println(follow)
        checkLL1()
    }

    private fun checkArgumentLengths() {
        val nonTerminalArgLengths = nonTerminalRules.associateBy { it.name }
            .mapValues { (_, rule) -> rule.arguments.size }
        for (rule in nonTerminalRules) {
            for (production in rule.productions) {
                for (atom in production.atoms) {
                    if (isTerminal(atom)) {
                        continue
                    }
                    val lhs = atom.arguments.size
                    val rhs = nonTerminalArgLengths[atom.name]
                    if (lhs != rhs) {
                        throw GrammarException(
                            "Atom ${atom.name} called in rule ${rule.name} with $lhs arguments, but should have $rhs arguments"
                        )
                    }
                }
            }
        }
    }

    fun first1(production: Production, ruleName: String): Set<String> {
        val retval = getFirst(production).toMutableSet()
        if (retval.contains(EPS)) {
            retval.addAll(follow[ruleName]!!)
            retval.remove(EPS)
        }
        return retval
    }

    private fun computeFollow() {
        follow[startNonTerminal] = mutableSetOf(END)
        var changed = true
        while (changed) {
            changed = false
            for (rule in nonTerminalRules) {
                for (production in rule.productions) {
                    if (production.atoms.isEmpty()) {
                        continue
                    }
                    val atoms = production.atoms
                    changed = computeFollowForAtoms(atoms, rule) || changed
                }
            }
        }
    }

    private fun computeFollowForAtoms(
        atoms: List<Atom>,
        rule: NonTerminalRule
    ): Boolean {
        var changed = false
        for ((i, atom) in atoms.withIndex()) {
            if (isTerminal(atom)) {
                continue
            }
            val prevFollow = follow.getOrPut(atom.name, ::mutableSetOf)
            val gamma = getFirst(atoms.drop(i + 1))
            val prevSize = prevFollow.size
            if (gamma.contains(EPS)) {
                prevFollow.addAll(follow.getOrPutEmpty(rule.name))
            }
//            println("Putting $gamma (gamma=${atoms.drop(i)}) at $i-th atom $atom by atoms $atoms, rule name ${rule.name}.")
            prevFollow.addAll(gamma)
            prevFollow.remove(EPS)
            if (prevFollow.size != prevSize) {
                changed = true
            }
        }
        return changed
    }

    fun isTerminal(s: Atom) = terminals.contains(s.name)

    private fun getFirst(production: Production) = getFirst(production.atoms)

    // TODO: memoize?
    private fun getFirst(atoms: List<Atom>): Set<String> {
        if (atoms.isEmpty()) {
            return setOf(EPS)
        }
        val firstAtom = atoms.first()
        if (isTerminal(firstAtom)) {
            return setOf(firstAtom.name)
        }
        val fst = first.getOrPut(firstAtom.name, ::mutableSetOf)
        if (fst.contains(EPS)) {
            fst.remove(EPS)
            fst.addAll(getFirst(atoms.drop(1)))
        }
        return fst
    }

    private fun computeFirst() {
        var changed = true
        while (changed) {
            changed = false
            for (rule in nonTerminalRules) {
                for (production in rule.productions) {
                    val prevSet = first.getOrPut(rule.name, ::mutableSetOf)
                    val prevSize = prevSet.size
                    prevSet.addAll(getFirst(production))
                    if (prevSet.size != prevSize) {
                        changed = true
                    }
                }
            }
        }
    }

    private fun checkLL1() {
        val isLL1 = nonTerminalRules.all { checkRuleLL1(it) }
        if (!isLL1) {
            throw GrammarException("Grammar is not LL(1)")
        }
    }

    private fun checkRuleLL1(rule: NonTerminalRule): Boolean {
        val firsts = rule.productions.map { getFirst(it) }
        for ((i, lhs) in firsts.withIndex()) {
            for (rhs in firsts.filterIndexed { idx, _ -> idx != i }) {
                if (rhs.intersects(lhs)) {
                    return false
                }
                if (lhs.contains(EPS) && follow[rule.name]!!.intersects(lhs)) {
                    return false
                }
            }
        }
        return true
    }

    private fun checkUndefinedAtoms() {
        val atoms = (nonTerminalRules + terminalRules).map { it.name }.toSet()
        val undefinedAtom = nonTerminalRules.flatMap { it.productions }
            .flatMap { it.atoms }
            .firstOrNull { !atoms.contains(it.name) }
        if (undefinedAtom != null) {
            throw GrammarException("Undefined atom ${undefinedAtom.name}")
        }
    }

    private fun checkDuplicateRules() {
        for ((rules, ruleType) in listOf(terminalRules to "terminal", nonTerminalRules to "non-terminal")) {
            val ruleNames = rules.map { it.name }
            val distinctRuleNames = ruleNames.distinct()
            if (distinctRuleNames.size != ruleNames.size) {
                val diff = ruleNames.subtract(distinctRuleNames.toSet())
                throw GrammarException("Same $ruleType defined multiple times: $diff")
            }
        }
    }
}