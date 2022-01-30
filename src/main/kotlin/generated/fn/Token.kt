package generated.fn

enum class TokenType(val regex: Regex) {
    FN(Regex("fun")),
    LPAREN(Regex("\\(")),
    RPAREN(Regex("\\)")),
    NAME(Regex("[A-z]+")),
    COMMA(Regex(",")),
    COLON(Regex(":")),
    END(Regex("(?!x)x"))
}

data class Token(val text: String, val type: TokenType)
