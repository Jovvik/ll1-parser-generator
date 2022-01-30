package generated.math

enum class TokenType(val regex: Regex) {
    PLUS(Regex("\\+")),
    MINUS(Regex("-")),
    POW(Regex("\\*\\*")),
    MUL(Regex("\\*")),
    DIV(Regex("/")),
    NUM(Regex("[0-9]+")),
    LPAREN(Regex("\\(")),
    RPAREN(Regex("\\)")),
    END(Regex("(?!x)x"))
}

data class Token(val text: String, val type: TokenType)
