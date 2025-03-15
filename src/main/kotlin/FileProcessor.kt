import java.io.File

class FileProcessor(private val filePath: String) {

    fun getKotlinFiles(): List<File> {
        val file = File(filePath)
        if (!file.exists()) {
            System.err.println("Error: '$filePath' does not exist.")
            return emptyList()
        }

        return if (file.isDirectory) {
            getKotlinFilesRecursively(file)
        } else {
            listOf(file)
        }
    }

    private fun getKotlinFilesRecursively(dir: File): List<File> {
        val kotlinFiles = mutableListOf<File>()
        dir.walk().forEach {
            if (it.isFile && (it.extension == "kt" || it.extension == "kts")) {
                kotlinFiles.add(it)
            }
        }
        return kotlinFiles
    }
}