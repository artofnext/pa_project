package gson_gen

import org.junit.Assert.*
import org.junit.Test
import kotlin.test.assertSame
import kotlin.test.assertTrue

class Stage1KtTest {
    //Mocked objects
    //node that contains object
    var rootNode1: Jnode
    //node that contains array
    var rootNode2: Jnode
    //node4
    var node4: Jnode
    init {
        val myArray = Jarray(
            mutableListOf<Jvalue>(
                Jstring("one"),
                Jstring("two"),
                Jstring("three")
            )
        )
        val myObj = Jobject(
            mutableListOf<Jnode>(
                Jnode("node01", Jstring("node value")),
                Jnode("node02", Jstring("node value")),
                Jnode("node03", Jstring("node value"))
            )
        )

        val myNode1 = Jnode("myNode1", myObj)
        val myNode2 = Jnode("myNode2", myArray)

        val rootObj = Jobject(mutableListOf<Jnode>(myNode1, myNode2))
        node4 = Jnode("node04", Jnumber(5.0))
        rootObj.addNode(node4)
        val rootArray = Jarray(mutableListOf<Jvalue>(myArray, myObj))
        rootArray.addValue(Jobject(mutableListOf(Jnode("node05", Jstring("node 05 value")))))
        rootObj.addNode(Jnode("node06", Jbool(true)))
        rootObj.addNode(Jnode("node07", Jnull()))

        //node that contains object
        rootNode1 = Jnode(value = rootObj)
        //node that contains array
        rootNode2 = Jnode(value = rootArray)
    }

    //Stage 1.1 Serialization
    //Visitor class - generate JSON string
    class StringifyVisitor : Visitor {
        var str = ""
        override fun visit(node: Jnode) {
            str += " \"${node.key}\": "
        }

        override fun visit(obj: Jobject) {
            str += "{"
        }

        override fun visit(arr: Jarray) {
            str += "["
        }

        override fun visit(value: Jstring) {
            str += '"' + value.value + '"'
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
            str += ""
        }

        override fun afterVisit(value: Jobject) {
            str = str.dropLast(1) // delete trailing comma
            str += " },"
        }

        override fun afterVisit(value: Jarray) {
            str = str.dropLast(1) // delete trailing comma
            str += " ],"
        }

        override fun afterVisit(value: Jstring) {
            str += ","
        }

        override fun afterVisit(value: Jnumber) {
            str += ","
        }

        override fun afterVisit(value: Jbool) {
            str += ","
        }

        override fun afterVisit(value: Jnull) {
            str += ","
        }
    }
    @Test
    fun serializationTest() {
        // use Visitor for JSON serialization
        val myStrVisitor1 = StringifyVisitor()
        //node that contains object
        rootNode1.accept(myStrVisitor1)
        val expected1 = " \"root\": { \"myNode1\": { \"node01\": \"node value\", \"node02\": \"node value\", \"node03\": \"node value\" }, \"myNode2\": [\"one\",\"two\",\"three\" ], \"node04\": 5.0, \"node06\": true, \"node07\": null },"
        assertEquals(expected1, myStrVisitor1.str)

        val myStrVisitor2 = StringifyVisitor()
        rootNode2.accept(myStrVisitor2)
        val expected2 = " \"root\": [[\"one\",\"two\",\"three\" ],{ \"node01\": \"node value\", \"node02\": \"node value\", \"node03\": \"node value\" },{ \"node05\": \"node 05 value\" } ],"
        assertEquals(expected2, myStrVisitor2.str)
    }

    //Stage 1.2.1 Searching all strings
    class findStringsVisitor():Visitor {
        var strs = mutableListOf<String>()
        override fun visit(value: Jnode) {
        }

        override fun visit(value: Jobject) {
        }

        override fun visit(value: Jarray) {
        }

        override fun visit(value: Jstring) {
            strs.add(value.toString().trim { i -> i == '"' }) //remove parentheses
        }

        override fun visit(value: Jnumber) {
        }

        override fun visit(value: Jbool) {
        }

        override fun visit(value: Jnull) {
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
    @Test
    fun findStringsTest() {
        // find all strings in Jobject
        val myStrVisitor1 = findStringsVisitor()
        //node that contains object
        rootNode1.accept(myStrVisitor1)
        val expected1 = arrayListOf<String>("node value", "node value", "node value", "one", "two", "three")
        assertEquals(expected1, myStrVisitor1.strs)
    }

    @Test
    fun filterTest() {
        // filter with predicate
        val allStrings = rootNode1.filter { it::class == Jstring::class  }
        val expected ="[\"node value\", \"node value\", \"node value\", \"one\", \"two\", \"three\"]"
        assertEquals(expected, allStrings.toString())
    }

    //Stage 1.2.2 Searching by properties
    class findObjByNameVisitor(val name: String):Visitor {

        var objs = mutableListOf<Jvalue>()

        override fun visit(value: Jnode) {
            if (value.key == name) {
                objs.add(value)
            }
        }

        override fun visit(value: Jobject) {
        }

        override fun visit(value: Jarray) {
        }

        override fun visit(value: Jstring) {
        }

        override fun visit(value: Jnumber) {
        }

        override fun visit(value: Jbool) {
        }

        override fun visit(value: Jnull) {
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
    @Test
    fun findValByNameTest() {
        // find all properties by name
        val findObjVisitor = findObjByNameVisitor("node04")
        rootNode1.accept(findObjVisitor)
        val expected1 = arrayListOf(node4)
        assertEquals(expected1, findObjVisitor.objs)
    }
}