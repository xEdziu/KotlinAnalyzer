import org.junit.jupiter.api.*
import java.io.File
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainTest {

    private val testResourcesPath = "src/test/resources"
    private val originalOut = System.out
    private val originalErr = System.err
    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var errorStream: ByteArrayOutputStream

    @BeforeEach
    fun setUp() {
        outputStream = ByteArrayOutputStream()
        errorStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))  // Capture standard output
        System.setErr(PrintStream(errorStream))  // Capture error output
    }

    @AfterEach
    fun tearDown() {
        System.setOut(originalOut)  // Restore standard output
        System.setErr(originalErr)  // Restore error output
    }

    @Test
    fun `should extract public functions and classes`() {
        val testFile = File("$testResourcesPath/test1.kt")
        runMainWithArgs(testFile.absolutePath)

        val expectedOutput = """
            fun publicFunction()
            class PublicClass {
                fun method()
            }
        """.trimIndent()

        assertEquals(expectedOutput, getOutput().trim())
    }

    @Test
    fun `should ignore private functions and classes`() {
        val testFile = File("$testResourcesPath/test_private.kt")
        runMainWithArgs(testFile.absolutePath)

        assertTrue(getOutput().trim().isEmpty(), "Expected no public declarations, but got: ${getOutput()}")
    }

    @Test
    fun `should handle empty Kotlin file`() {
        val testFile = File("$testResourcesPath/test_empty.kt")
        runMainWithArgs(testFile.absolutePath)

        assertTrue(getOutput().trim().isEmpty(), "Expected empty output for an empty file.")
    }

    @Test
    fun `should handle mixed visibility declarations`() {
        val testFile = File("$testResourcesPath/test_mixed.kt")
        runMainWithArgs(testFile.absolutePath)

        val expectedOutput = """
            fun publicFunction()
            class PublicClass {
                val publicProperty: String
            }
        """.trimIndent()

        assertEquals(expectedOutput, getOutput().trim())
    }

    @Test
    fun `should return error for non-existent directory`() {
        val nonExistentDir = "non_existent_directory"
        runMainWithArgs(nonExistentDir)

        assertTrue(getErrorOutput().contains("Error"), "Expected error message but got: ${getErrorOutput()}")
    }

    @Test
    fun `should return empty output for directory with no Kotlin files`() {
        val emptyDir = File("$testResourcesPath/empty_folder")
        runMainWithArgs(emptyDir.absolutePath)

        assertTrue(getOutput().trim().isEmpty(), "Expected empty output for a folder with no Kotlin files.")
    }

    // Uruchamia Main.kt z podanym argumentem
    private fun runMainWithArgs(vararg args: String) {
        main(args)
    }



    // Pobiera przechwycone wyjście programu
    private fun getOutput(): String {
        return outputStream.toString()
    }

    // Pobiera przechwycone wyjście błędów
    private fun getErrorOutput(): String {
        return errorStream.toString()
    }
}