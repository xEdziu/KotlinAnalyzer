import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

class FileParser {

    private val disposable = Disposer.newDisposable()
    private val config = CompilerConfiguration()
    private val environment = KotlinCoreEnvironment.createForProduction(disposable, config, EnvironmentConfigFiles.JVM_CONFIG_FILES)
    private val project = environment.project

    /**
     * Parses a Kotlin file and returns a KtFile object.
     * @param file The file to parse.
     * @return The KtFile object, or null if the file could not be parsed.
     */
    fun parseFile(file: File): KtFile? {
        return try {
            val text = file.readText()
            val virtualFile = LightVirtualFile(file.name, KotlinFileType.INSTANCE, text)
            PsiManager.getInstance(project).findFile(virtualFile) as? KtFile
        } catch (e: Exception) {
            System.err.println("Warning: Could not read/parse file ${file.path}: ${e.message}")
            null
        }
    }
}