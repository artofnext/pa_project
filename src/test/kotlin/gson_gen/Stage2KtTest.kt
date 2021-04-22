package gson_gen

import junit.framework.Assert.assertEquals
import org.junit.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue


internal class Stage2KtTest {

    val person1: Person = Person("Mary", 23)
    val props2: Props = Props("approved", person1)
    val props1: Props = Props("approved", props2)
    val user1 = User("Alex", 22, props1)
    val user2 = User1("Sam", 25.0)
    val user2Jnode = Jnode(value = Jobject(mutableListOf(Jnode("age", Jnumber((25).toDouble())), Jnode("name", Jstring("Sam")))))

//    @Test
//    fun toJvalue() {
//
//    }
//
//    @Test
//    fun testToJvalue() {
//    }
//
//    @Test
//    fun testToJvalue1() {
//    }
//
//    @Test
//    fun testToJvalue2() {
//    }
//
//    @Test
//    fun testToJvalue3() {
//    }
//
//    @Test
//    fun testToJvalue4() {
//    }
//
//    @Test
//    fun testToJvalue5() {
//    }

    @Test
    fun testDataClassToJnode() {
//        val expected: String =
//            "\"root\": { \"age\": \"22\", \"isGood\": \"true\", \"name\": \"Alex\", \"number\": \"10\", \"props\": { \"aux\": { \"Person\": \"is not a data class\", \"status\": \"approved\" }, \"status\": \"approved\" }, \"sity\": null }"
//        assertEquals(expected, dataClassToJnode(user1).toString())

        val expected1: Jnode = user2Jnode
//        assertEquals(expected1, dataClassToJnode(user2))
//        assertEquals(expected1, dataClassToJnode(user2))
        assertTrue { expected1 == dataClassToJnode(user2) }

    }

//    @Test
//    fun toJarray() {
//    }
}
//
//fun main() {
//    val test1 = Stage2KtTest()
//    test1.testDataClassToJnode()
//
//    println(test1.user2Jnode.toString())
//    println(dataClassToJnode(test1.user2))
//
//}
