import java.nio.file.Path

abstract class Generator(protected val grammar: Grammar) {
    fun generate(path: Path) {
        CodeWriter(path.resolve("$fileName.kt"), grammar).use { writer ->
            writer.main()
        }
    }

    abstract fun CodeWriter.main()

    protected abstract val fileName: String
}