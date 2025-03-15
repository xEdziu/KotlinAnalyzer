fun main(args: Array<out String>) {
    val filePath = if (args.isNotEmpty()) args[0] else {
        "src/main/resources/testDir"
    }

    val fileProcessor = FileProcessor(filePath)
    val kotlinFiles = fileProcessor.getKotlinFiles()

    if (kotlinFiles.isEmpty()) {
        System.err.println("Error: No Kotlin files found in '$filePath'.")
        return
    }

    val kotlinFileParser = FileParser()
    val declarationProcessor = DeclarationProcessor()

    kotlinFiles.forEach { file ->
        val ktFile = kotlinFileParser.parseFile(file)
        ktFile?.let {
            val output = declarationProcessor.processDeclarations(it)
            println(output)
        }
    }
}