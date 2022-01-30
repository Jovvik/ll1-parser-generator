import generated.math.Lexer
import generated.math.Parser
//import generated.fn.Lexer
//import generated.fn.Parser
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File
import java.nio.file.Path

fun main(args: Array<String>) {
    val input = Path.of("build","resources", "main", "math.gr")
    val lexer = GrammarLexer(CharStreams.fromPath(input))
    val parser = GrammarParser(CommonTokenStream(lexer))
    val grammar = parser.file().grammar
    val output = Path.of("src", "main", "kotlin", "generated", grammar.name)
    for (gen in listOf(
        ExceptionGenerator(grammar),
        TokenGenerator(grammar),
        LexerGenerator(grammar),
        ParserGenerator(grammar)
    )) {
        gen.generate(output)
    }

    val ourLexer = Lexer(Path.of("build","resources", "main", "math.txt"))
//    var parsedToken = ourLexer.nextToken()
//    while (parsedToken.type != TokenType.END) {
//        println(parsedToken)
//        parsedToken = ourLexer.nextToken()
//    }
//    println(parsedToken)
    val ourParser = Parser(ourLexer)
    val g = ourParser.parse()
    println(g)
//    Graphviz.fromGraph(g).width(1000).render(Format.PNG).toFile(File("fn.png"))
}