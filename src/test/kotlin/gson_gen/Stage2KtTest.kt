package gson_gen

import junit.framework.Assert.assertEquals
//import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertTrue


internal class Stage2KtTest {

    // test classes

    data class User(val name: String, val age: Int, val props: Any?) {
        val sity = null
        @Rename("renamed")
        val number = 10
        @Remove
        val isGood = true
    }

    data class User1(val name: String, val age: Double)

    data class Props(val status: String, val aux: Any)

    class Person(val name: String, val age: Int)

    enum class Direction {
        NORTH, SOUTH, WEST, EAST
    }

    enum class Numbers(val number: Number) {
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4)
    }

    class SerialiseVisitor: Visitor {
        var str = ""
        override fun visit(value: Jnode) {
            str += value.key + ":"
        }

        override fun visit(value: Jobject) {

        }

        override fun visit(value: Jarray) {

        }

        override fun visit(value: Jstring) {
            str += value.value
        }

        override fun visit(value: Jnumber) {
            str += value.value.toString()
        }

        override fun visit(value: Jbool) {
            str += value.value.toString()
        }

        override fun visit(value: Jnull) {
            str += "null"
        }

        override fun afterVisit(value: Jnode) {

        }

        override fun afterVisit(value: Jobject) {

        }

        override fun afterVisit(value: Jarray) {

        }

        override fun afterVisit(value: Jstring) {

        }

        override fun afterVisit(value: Jnumber) {

        }

        override fun afterVisit(value: Jbool) {

        }

        override fun afterVisit(value: Jnull) {

        }

    }

    var i = Direction.values()

    val person1: Person = Person("Mary", 23)
    val props2: Props = Props("approved", person1)
    val props1: Props = Props("approved", props2)
    val user1 = User("Alex", 22, props1)
    val user2 = User1("Sam", 25.0)
    val user2Jnode = Jobject(mutableListOf(Jnode("age", Jnumber((25).toDouble())), Jnode("name", Jstring("Sam"))))

    @Test
    fun getJvalueTest() {
        assertTrue(getJvalue(5) is Jnumber)
        assertTrue(getJvalue("string") is Jstring)
        assertTrue(getJvalue(true) is Jbool)
        assertTrue(getJvalue(user2) is Jobject)
    }

    @Test
    fun testToJvalueList() {
        // lists comparison by strings
        val strVis1 = SerialiseVisitor()
        val strVis2 = SerialiseVisitor()
        // mocked object
        Jarray(mutableListOf(Jstring("one"),Jstring("two"),Jstring("tree"))).accept(strVis1)
        val expected1 = strVis1.str

        listOf("one","two","tree").toJvalue().accept(strVis2)
        assertEquals(expected1, strVis2.str)
    }

    @Test
    fun testToJvalueMap() {
        // todo
        // lists comparison by strings
        val strVis1 = SerialiseVisitor()
        val strVis2 = SerialiseVisitor()
        // mocked object
        Jarray(mutableListOf(Jstring("one"),Jstring("two"),Jstring("tree"))).accept(strVis1)
        val expected1 = strVis1.str

        listOf("one","two","tree").toJvalue().accept(strVis2)
        assertEquals(expected1, strVis2.str)
    }

    @Test
    fun testDataClassToJnode() {
        // object comparison by strings
        // let's use serializations of both objects, mocked and generated
        val strVis1 = SerialiseVisitor()
        val strVis2 = SerialiseVisitor()
        // mocked object
        user2Jnode.accept(strVis1)
        // generated object from data class user2
        dataClassToJobject(user2).accept(strVis2)
        val expected1: String = strVis1.str
        // strings should be equal
        assertEquals(expected1, strVis2.str)

        val strVis3 = SerialiseVisitor()
        dataClassToJobject(user1).accept(strVis3)
        val expected2 = "age:22name:Alexrenamed:10props:aux:aux:is not a data classstatus:approvedstatus:approvedsity:null"
        assertEquals(expected2, strVis3.str)
    }
}

