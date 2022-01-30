package generated.fn

import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.MutableGraph
import guru.nidi.graphviz.model.MutableNode
import guru.nidi.graphviz.attribute.Label
import generated.fn.TokenType.*
import kotlin.math.pow

class Parser(private val lexer: Lexer) {
    private var nodeCount = 0
    private fun node(label: String) = nodeCount++.let { mutNode(nodeCount.toString()).add(Label.of(label)) }
    private lateinit var curToken: Token
    init { nextToken() }
    private fun nextToken() {
        curToken = lexer.nextToken()
    }
    fun parse(): MutableGraph {
        val result = parseh()
        if (curToken.type != END) {
            throw ParserException("Expected END, but found ${curToken.type} with text ${curToken.text}")
        }
        return result
    }
    private fun parseh(): MutableGraph {
        when (curToken.type) {
            FN -> {
                val g = mutGraph("fun").setDirected(true); val h=mutNode("h"); g.add(h)
                val fn = curToken.text
                nextToken()
                h.addLink(mutNode(fn))
                if (curToken.type != NAME) {
                    throw ParserException("Expected NAME, but found ${curToken.type} with text ${curToken.text}")
                }
                val name = curToken.text
                nextToken()
                h.addLink(mutNode(name))
                if (curToken.type != LPAREN) {
                    throw ParserException("Expected LPAREN, but found ${curToken.type} with text ${curToken.text}")
                }
                val lparen = curToken.text
                nextToken()
                h.addLink(mutNode(lparen))
                val p = parsep()
                h.addLink(p)
                if (curToken.type != RPAREN) {
                    throw ParserException("Expected RPAREN, but found ${curToken.type} with text ${curToken.text}")
                }
                val rparen = curToken.text
                nextToken()
                h.addLink(mutNode(rparen))
                val r = parser()
                h.addLink(r); return g
            }
            else -> {
                throw ParserException("Expected one of: FN, got ${curToken.type}")
            }
        }
    }
    private fun parsep(): MutableNode {
        when (curToken.type) {
            NAME -> {
                val name = curToken.text
                nextToken()
                val t = parset()
                val pprime = parsepprime()
                return node("P").addLink(node(name)).addLink(t).addLink(pprime)
            }
            RPAREN -> {
                return node("P")
            }
            else -> {
                throw ParserException("Expected one of: NAME, RPAREN, got ${curToken.type}")
            }
        }
    }
    private fun parsepprime(): MutableNode {
        when (curToken.type) {
            COMMA -> {
                val comma = curToken.text
                nextToken()
                val p = parsep()
                return node("P'").addLink(node(comma)).addLink(p)
            }
            RPAREN -> {
                return node("P'")
            }
            else -> {
                throw ParserException("Expected one of: COMMA, RPAREN, got ${curToken.type}")
            }
        }
    }
    private fun parset(): MutableNode {
        when (curToken.type) {
            COLON -> {
                val colon = curToken.text
                nextToken()
                if (curToken.type != NAME) {
                    throw ParserException("Expected NAME, but found ${curToken.type} with text ${curToken.text}")
                }
                val name = curToken.text
                nextToken()
                nodeCount++; return node("T").addLink(node(colon)).addLink(node(name))
            }
            else -> {
                throw ParserException("Expected one of: COLON, got ${curToken.type}")
            }
        }
    }
    private fun parser(): MutableNode {
        when (curToken.type) {
            COLON -> {
                val t = parset()
                return node("R").addLink(t)
            }
            END -> {
                return node("R")
            }
            else -> {
                throw ParserException("Expected one of: COLON, END, got ${curToken.type}")
            }
        }
    }
}
