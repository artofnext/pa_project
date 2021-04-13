package gson_generator

abstract class Jvalue {
    abstract fun accept(v: Visitor)
}

interface Visitor {

    fun visit(value: Jnode)
    fun visit(value: Jobject)
    fun visit(value: Jarray)
    fun visit(value: Jstring)
    fun visit(value: Jnumber)
    fun visit(value: Jbool)
    fun visit(value: Jnull)
}

class StringifyVisitor : Visitor {
    var str = ""
    override fun visit(node: Jnode) {
        str += " ${node.key}: "
    }

    override fun visit(obj: Jobject) {
        str += "{ "
    }

    override fun visit(arr: Jarray) {
        str += "[ "
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
}

class Jnode (key: String = "root", value: Jvalue): Jvalue() {
    var key: String = key
    var value: Jvalue = value

    override fun accept(v: Visitor) {
        v.visit(this)
        value.accept(v)
    }

    override fun toString(): String {
        var str = "${key}: "
        str += value.toString()
        return str
    }

}

class Jobject (var value: MutableList<Jnode>): Jvalue() {

    fun addNode(elem: Jnode) {
        value.add(elem)
    }

    override fun accept(v: Visitor) {
        v.visit(this)
        value.forEach { it.accept(v) }.also { println("Jobject accept visitor") }
    }

    override fun toString(): String {
        var str = "{ "
        str += value.joinToString(", ") {it.toString()}
        str += " }"
        return str
    }
}

class Jarray (var value: MutableList<Jvalue>): Jvalue() {

    fun addValue(elem: Jvalue) {
        value.add(elem)
    }

    override fun accept(v: Visitor) {
        v.visit(this)
        value.forEach { it.accept(v) }
    }

    override fun toString(): String {
        var str = "[ "
        str += value.joinToString(", ") {it.toString()}
        str += " ]"
        return str
    }
}

class Jstring(var value: String): Jvalue() {

    override fun accept(v: Visitor) {
        v.visit(this)
    }

    override fun toString(): String {
        return '"' + value + '"'
    }
}

class Jnumber (var value: Double): Jvalue() {

    override fun accept(v: Visitor) {
        v.visit(this)
    }

    override fun toString(): String {
        return value.toString()
    }
}

class Jbool (var value: Boolean): Jvalue() {

    override fun accept(v: Visitor) {
        v.visit(this)
    }

    override fun toString(): String {
        return value.toString()
    }
}

class Jnull: Jvalue() {

    override fun accept(v: Visitor) {
        v.visit(this)
    }

    override fun toString(): String {
        return "null"
    }
}


fun main() {
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

    var rootObj = Jobject(mutableListOf<Jnode>(myNode1, myNode2))
    rootObj.addNode(Jnode("node04", Jnumber(5.0)))
    var rootArray = Jarray(mutableListOf<Jvalue>(myArray, myObj))
    rootArray.addValue(Jobject(mutableListOf(Jnode("node05", Jstring("node 05 value")))))

    var rootNode1 = Jnode(value = rootObj)
    var rootNode2 = Jnode(value = rootArray)

    val jsonStr = rootNode1.toString()
    println(jsonStr)

    val myStrVisitor = StringifyVisitor()

    rootNode1.accept(myStrVisitor)
    println(myStrVisitor.str)

}