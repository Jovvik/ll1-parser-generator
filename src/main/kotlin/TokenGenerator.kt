class TokenGenerator(grammar: Grammar) : Generator(grammar) {
    override val fileName = "Token"
    override fun CodeWriter.main() {
        block("enum class TokenType(val regex: Regex)") {
            for (rule in grammar.terminalRules) {
                write("${rule.name}(Regex(\"${rule.regex}\")),")
            }
            write("END(Regex(\"(?!x)x\"))")
        }
        write()
        write("data class Token(val text: String, val type: TokenType)")
    }
}