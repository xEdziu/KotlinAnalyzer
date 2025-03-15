package testDir

class Test {
    public fun publicFunction() {
        println("publicFunction")
    }
}

private fun privateFunction() {
    println("privateFunction")
}

internal fun internalFunction() {
    println("internalFunction")
}

fun Function() {
    println("protectedFunction")
}

class PublicClass {
    public fun method() {
        println("method")
    }

    protected  fun protectedMethod() {
        println("protectedMethod")
    }
}