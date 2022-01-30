package generated.math

import generated.math.TokenType.*
import kotlin.math.pow

class Parser(private val lexer: Lexer) {
    private lateinit var curToken: Token
    init { nextToken() }
    private fun nextToken() {
        curToken = lexer.nextToken()
    }
    fun parse(): Int {
        val result = parseexpr()
        if (curToken.type != END) {
            throw ParserException("Expected END, but found ${curToken.type} with text ${curToken.text}")
        }
        return result
    }
    private fun parseexpr(): Int {
        when (curToken.type) {
            NUM, LPAREN, MINUS -> {
                val term = parseterm()
                val exprP = parseexprP(term)
                return exprP
            }
            else -> {
                throw ParserException("Expected one of: NUM, LPAREN, MINUS, got ${curToken.type}")
            }
        }
    }
    private fun parseexprP(acc: Int): Int {
        when (curToken.type) {
            PLUS -> {
                val plus = curToken.text
                nextToken()
                val term = parseterm()
                val newAcc = acc + term
                val exprP = parseexprP(newAcc)
                return exprP
            }
            MINUS -> {
                val minus = curToken.text
                nextToken()
                val term = parseterm()
                val newAcc = acc - term
                val exprP = parseexprP(newAcc)
                return exprP
            }
            END, RPAREN -> {
                return acc
            }
            else -> {
                throw ParserException("Expected one of: PLUS, MINUS, END, RPAREN, got ${curToken.type}")
            }
        }
    }
    private fun parseterm(): Int {
        when (curToken.type) {
            NUM, LPAREN, MINUS -> {
                val pow = parsepow()
                val termP = parsetermP(pow)
                return termP
            }
            else -> {
                throw ParserException("Expected one of: NUM, LPAREN, MINUS, got ${curToken.type}")
            }
        }
    }
    private fun parsetermP(acc: Int): Int {
        when (curToken.type) {
            MUL -> {
                val mul = curToken.text
                nextToken()
                val pow = parsepow()
                val newAcc = acc * pow
                val termP = parsetermP(newAcc)
                return termP
            }
            DIV -> {
                val div = curToken.text
                nextToken()
                val pow = parsepow()
                val newAcc = acc / pow
                val termP = parsetermP(newAcc)
                return termP
            }
            END, PLUS, MINUS, RPAREN -> {
                return acc
            }
            else -> {
                throw ParserException("Expected one of: MUL, DIV, END, PLUS, MINUS, RPAREN, got ${curToken.type}")
            }
        }
    }
    private fun parsepow(): Int {
        when (curToken.type) {
            NUM, LPAREN, MINUS -> {
                val factor = parsefactor()
                val powP = parsepowP()
                return factor.toDouble().pow(powP).toInt()
            }
            else -> {
                throw ParserException("Expected one of: NUM, LPAREN, MINUS, got ${curToken.type}")
            }
        }
    }
    private fun parsepowP(): Int {
        when (curToken.type) {
            POW -> {
                val pow = curToken.text
                nextToken()
                val factor = parsefactor()
                val powP = parsepowP()
                return factor.toDouble().pow(powP).toInt()
            }
            END, PLUS, MINUS, MUL, DIV, RPAREN -> {
                return 1
            }
            else -> {
                throw ParserException("Expected one of: POW, END, PLUS, MINUS, MUL, DIV, RPAREN, got ${curToken.type}")
            }
        }
    }
    private fun parsefactor(): Int {
        when (curToken.type) {
            NUM -> {
                val num = curToken.text
                nextToken()
                return num.toInt()
            }
            LPAREN -> {
                val lparen = curToken.text
                nextToken()
                val expr = parseexpr()
                if (curToken.type != RPAREN) {
                    throw ParserException("Expected RPAREN, but found ${curToken.type} with text ${curToken.text}")
                }
                val rparen = curToken.text
                nextToken()
                return expr
            }
            MINUS -> {
                val minus = curToken.text
                nextToken()
                val factor = parsefactor()
                return -factor
            }
            else -> {
                throw ParserException("Expected one of: NUM, LPAREN, MINUS, got ${curToken.type}")
            }
        }
    }
}
