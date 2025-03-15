package testDir.nestedDir

class NestedTest {
    fun test() {
        println("NestedTest")
    }
}

fun main() {
    println("Main")
}

fun test() : String {
    println("Test")
}

public class PublicClass {

    val publicProperty: String = "PublicProperty"
    private val privateProperty: String = "PrivateProperty"

    fun publicMethod() {
        println("PublicMethod")
    }
}