class ParserGenerator(grammar: Grammar) : Generator(grammar) {
    override val fileName = "Parser"
    override fun CodeWriter.main() {
        writeMaybe(grammar.header)
        write(
            "import generated.${grammar.name}.TokenType.*",
            "import kotlin.math.pow",
            ""
        )
        block("class Parser(private val lexer: Lexer)") {
            writeMaybe(grammar.fields)
            write(
                "private lateinit var curToken: Token",
                "init { nextToken() }"
            )
            block("private fun nextToken()") {
                write("curToken = lexer.nextToken()")
            }
            block("fun parse()${getReturnTypeString(grammar.startRule)}") {
                write("val result = parse${grammar.startNonTerminal}()")
                assertToken("END")
                write("return result")
            }
            grammar.nonTerminalRules.forEach { writeRule(it) }
        }
    }

    private fun CodeWriter.assertToken(tokenName: String) {
        block("if (curToken.type != $tokenName)") {
            write("throw ParserException(\"Expected $tokenName, but found \${curToken.type} with text \${curToken.text}\")")
        }
    }

    private fun CodeWriter.writeRule(rule: NonTerminalRule) {
        val funArgs = rule.arguments.joinToString { "${it.name}: ${it.type}" }
        block("private fun parse${rule.name}($funArgs)${getReturnTypeString(rule)}") {
            block("when (curToken.type)") {
                val validTokens = mutableListOf<String>()
                for (production in rule.productions) {
                    validTokens.addAll(writeProduction(production, rule.name))
                }
                block("else ->") {
                    write("throw ParserException(\"Expected one of: ${validTokens.joinToString()}, got \${curToken.type}\")")
                }
            }
        }
    }

    private fun getReturnTypeString(rule: NonTerminalRule) = if (rule.returnType == null) "" else ": ${rule.returnType}"

    private fun CodeWriter.writeProduction(production: Production, ruleName: String): Set<String> {
        val first1 = grammar.first1(production, ruleName)
        block(first1.joinToString() + " ->") {
            for ((idx, atom) in production.atoms.withIndex()) {
                writeMaybe(atom.code)
                if (grammar.isTerminal(atom)) {
                    if (idx != 0) {
                        assertToken(atom.name)
                    }
                    write(
                        "val ${atom.name.lowercase()} = curToken.text",
                        "nextToken()"
                    )
                } else {
                    write("val ${atom.name} = parse${atom.name}(${atom.arguments.joinToString()})")
                }
            }
            writeMaybe(production.finalCode)
        }
        return first1
    }
}