package gson_gen

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class Stage2KtTest {

    val person1: Person = Person("Mary", 23)
    val props2: Props = Props("approved", person1)
    val props1: Props = Props("approved", props2)
    val user1 = User("Alex", 22, props1)

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
        val expected: String =
            "\"root\": { \"age\": \"22\", \"isGood\": \"true\", \"name\": \"Alex\", \"number\": \"10\", \"props\": { \"aux\": { \"Person\": \"not data class\", \"status\": \"approved\" }, \"status\": \"approved\" }, \"sity\": null }"
        assertEquals(expected, dataClassToJnode(user1).toString())
    }

//    @Test
//    fun toJarray() {
//    }
}