import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.lexer.KtTokens
import java.io.File


fun main(args: Array<out String>) {

    val filePath = if (args.isNotEmpty()) args[0] else {
        "src/main/resources/testDir"
    }

    val disposable = Disposer.newDisposable()
    val config = CompilerConfiguration()
    val environment = KotlinCoreEnvironment.createForProduction(disposable, config, EnvironmentConfigFiles.JVM_CONFIG_FILES)
    val project = environment.project

    val file = File(filePath)
    if (!file.exists()) {
        System.err.println("Error: '$filePath' does not exist.")
        return
    }

    val kotlinFiles = if (file.isDirectory) {
        getKotlinFilesRecursively(file)
    } else {
        listOf(file)
    }

    if (kotlinFiles.isEmpty()) {
        System.err.println("Error: No Kotlin files found in '$filePath'.")
        return
    }

    val outputBuilder = StringBuilder()

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

    fun indent(level: Int) = "    ".repeat(level)

    fun processDeclaration(decl: KtDeclaration, containerIsPublic: Boolean, level: Int) {
        if (!containerIsPublic) return

        val isPrivate = decl.hasModifier(KtTokens.PRIVATE_KEYWORD)
        val isInternal = decl.hasModifier(KtTokens.INTERNAL_KEYWORD)
        val isProtected = decl.hasModifier(KtTokens.PROTECTED_KEYWORD)
        if (isPrivate || isInternal || isProtected) return

        when (decl) {
            is KtNamedFunction -> {
                val name = decl.name ?: "<anonymous>"
                val params = decl.valueParameters.joinToString { "${it.name}: ${it.typeReference?.text ?: "Any"}" }
                val returnType = decl.typeReference?.text ?: ""
                if (returnType == "") {
                    outputBuilder.append(indent(level)).append("fun $name($params)\n")
                } else {
                    outputBuilder.append(indent(level)).append("fun $name($params): $returnType\n")
                }
            }
            is KtProperty -> {
                val name = decl.name ?: "<anonymous>"
                val type = decl.typeReference?.text ?: "Unknown"
                val varOrVal = if (decl.isVar) "var" else "val"
                outputBuilder.append(indent(level)).append("$varOrVal $name: $type\n")
            }
            is KtClassOrObject -> {
                val keyword = when {
                    decl is KtObjectDeclaration && decl.isCompanion() -> "object"
                    decl is KtObjectDeclaration -> "object"
                    decl is KtClass && decl.isInterface() -> "interface"
                    decl is KtClass && decl.isEnum() -> "enum class"
                    decl is KtClass && decl.isData() -> "data class"
                    else -> "class"
                }
                val name = decl.name ?: "<anonymous>"
                outputBuilder.append(indent(level)).append("$keyword $name {\n")

                if (decl is KtClass) {
                    decl.primaryConstructor?.valueParameters?.forEach { param ->
                        if (param.hasValOrVar()) {
                            val propType = param.typeReference?.text ?: "Any"
                            outputBuilder.append(indent(level + 1)).append("${if (param.valOrVarKeyword?.text == "var") "var" else "val"} ${param.name}: $propType\n")
                        }
                    }
                }

                decl.declarations.forEach { processDeclaration(it, true, level + 1) }
                outputBuilder.append(indent(level)).append("}\n")
            }
            is KtTypeAlias -> {
                val name = decl.name ?: "<anonymous>"
                val aliasedType = decl.getTypeReference()?.text ?: "<?>"
                outputBuilder.append(indent(level)).append("typealias $name = $aliasedType\n")
            }
        }
    }

    kotlinFiles.forEach { file ->
        val ktFile = parseFile(file)
        ktFile?.declarations?.forEach { processDeclaration(it, true, 0) }
    }

    println(outputBuilder.toString())
}

fun getKotlinFilesRecursively(dir: File): List<File> {
    val kotlinFiles = mutableListOf<File>()
    dir.walk().forEach {
        if (it.isFile && (it.extension == "kt" || it.extension == "kts")) {
            kotlinFiles.add(it)
        }
    }
    return kotlinFiles
}