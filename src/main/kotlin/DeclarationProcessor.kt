import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.*

class DeclarationProcessor {

    private val outputBuilder = StringBuilder()

    fun processDeclarations(ktFile: KtFile): String {
        ktFile.declarations.forEach { processDeclaration(it, true, 0) }
        return outputBuilder.toString()
    }

    private fun processDeclaration(decl: KtDeclaration, containerIsPublic: Boolean, level: Int) {
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

    private fun indent(level: Int) = "    ".repeat(level)
}