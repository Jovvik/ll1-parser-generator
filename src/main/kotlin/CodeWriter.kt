import java.nio.file.Files
import java.nio.file.Path

class CodeWriter(path: Path, grammar: Grammar) : AutoCloseable {
    companion object {
        private const val PACKAGE_PREFIX = "generated"
        private const val TAB_LENGTH = 4
        private val TAB = " ".repeat(TAB_LENGTH)
    }

    private val name = grammar.name
    private val writer = Files.newBufferedWriter(path)

    init {
        writePackage()
    }

    private var indentationLevel = 0

    private fun incIndent() {
        indentationLevel++
    }

    private fun decIndent() {
        indentationLevel--
    }

    private fun blockStart(s: String) {
        write("$s {")
        incIndent()
    }

    private fun blockEnd() {
        decIndent()
        write("}")
    }

    fun write(vararg ss: String) {
        for (s in ss) {
            write(s)
        }
    }

    fun writeMaybe(s: String?) {
        s ?: return
        write(s)
    }

    private fun writeNoNewlines(ss: List<String>) {
        for (s in ss) {
            writer.write(TAB.repeat(indentationLevel))
            writer.write(s)
            writer.write(System.lineSeparator())
        }
    }

    fun write(s: String) {
        writeNoNewlines(s.split(System.lineSeparator()))
    }

    fun write() {
        writer.write(System.lineSeparator())
    }

    fun block(start: String, action: CodeWriter.() -> Unit) {
        blockStart(start)
        action()
        blockEnd()
    }

    private fun writePackage() {
        write("package $PACKAGE_PREFIX.$name")
        write()
    }

    override fun close() {
        writer.close()
    }
}