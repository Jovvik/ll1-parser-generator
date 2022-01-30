class ExceptionGenerator(grammar: Grammar) : Generator(grammar) {
    override val fileName = "Exceptions"
    override fun CodeWriter.main() {
        write(
            "class LexerException(message: String) : Exception(message)",
            "class ParserException(message: String) : Exception(message)"
        )
    }
}