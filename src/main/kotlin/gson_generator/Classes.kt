package gson_generator

abstract class Jvalue

class Jnode (key: String = "root", value: Jvalue) {
    var key: String = key
    var value: Jvalue = value

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

    override fun toString(): String {
        var str = "[ "
        str += value.joinToString(", ") {it.toString()}
        str += " ]"
        return str
    }
}

class Jstring(var value: String): Jvalue() {

    override fun toString(): String {
        return '"' + value + '"'
    }
}

class Jnumber (var value: Double): Jvalue() {

    override fun toString(): String {
        return value.toString()
    }
}

class Jbool (var value: Boolean): Jvalue() {

    override fun toString(): String {
        return value.toString()
    }
}

class Jnull: Jvalue() {

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

    println(myArray)
    println(myObj)
    println(myNode1)
    println("root node is object")
    println(rootNode1)
    println("root node is array")
    println(rootNode2)

}