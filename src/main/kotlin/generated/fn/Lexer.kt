package generated.fn

import java.nio.file.*
import java.util.stream.Collectors

class Lexer(input: Path) {
    private val skip = Regex("[ \\t\\r\\n]")
    private var curPos = 0
    private val data = Files.newBufferedReader(input)
        .use { it.lines().collect(Collectors.joining(System.lineSeparator())) }
        .let { skip.replace(it, "") }
    fun nextToken(): Token {
        if (curPos >= data.length) {
            return Token("", TokenType.END)
        }
        val (tokenType, match) = TokenType.values().map { it to it.regex.find(data, curPos) }
            .firstOrNull { it.second?.range?.first == curPos }
            ?: throw LexerException("Lexing failed at position $curPos")
        curPos = match!!.range.last + 1
        return Token(match.value, tokenType)
    }
}
