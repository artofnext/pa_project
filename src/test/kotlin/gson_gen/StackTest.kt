package gson_gen

import org.junit.Test

import org.junit.Assert.*

class StackTest {

    var testStack = Stack<Jvalue>()

    var myArray = Jarray(mutableListOf<Jvalue>(
        Jstring("one"),
        Jstring("two"),
        Jstring("three")
    ))
    var myObj = Jobject(mutableListOf<Jnode>(
        Jnode("node01", Jstring("node value")),
        Jnode("node02", Jstring("node value")),
        Jnode("node03", Jstring("node value"))
    ))

    var myNode1 = Jnode("myNode1", myObj)
    var myNode2 = Jnode("myNode2", myArray)

    init {

        testStack.push(myArray)
    }

    @Test
    fun testRead() {
        testStack.push(myArray)
        assertEquals(myArray, testStack.read())
    }

    @Test
    fun testPush() {
        testStack.push(myObj)
        assertEquals(myObj, testStack.read())
    }

    @Test
    fun testPull() {
        testStack.push(myNode1)
        assertEquals(myNode1, testStack.pull())
    }
}