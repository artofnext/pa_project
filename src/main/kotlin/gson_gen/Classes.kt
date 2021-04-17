package gson_gen

abstract class Jvalue {
    abstract fun accept(v: Visitor)
}

interface Visitor {

    fun visit(value: Jnode)
    fun afterVisit(value: Jnode)
    fun visit(value: Jobject)
    fun afterVisit(value: Jobject)
    fun visit(value: Jarray)
    fun afterVisit(value: Jarray)
    fun visit(value: Jstring)
    fun afterVisit(value: Jstring)
    fun visit(value: Jnumber)
    fun afterVisit(value: Jnumber)
    fun visit(value: Jbool)
    fun afterVisit(value: Jbool)
    fun visit(value: Jnull)
    fun afterVisit(value: Jnull)
}

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

class stringsVisitor():Visitor {
    var strs = mutableListOf<String>()
    override fun visit(value: Jnode) {
    }

    override fun visit(value: Jobject) {
    }

    override fun visit(value: Jarray) {
    }

    override fun visit(value: Jstring) {
        strs.add(value.toString())
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

class Jnode (key: String = "root", value: Jvalue): Jvalue() {
    var key: String = key
    var value: Jvalue = value

    override fun accept(v: Visitor) {
        v.visit(this)
        value.accept(v)
        v.afterVisit(this)
    }

    override fun toString(): String {
        var str = "\"${key}\": "
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
        value.forEach { it.accept(v) }
        v.afterVisit(this)
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
        v.afterVisit(this)
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
        v.afterVisit(this)
    }

    override fun toString(): String {
        return '"' + value + '"'
    }
}

class Jnumber (var value: Double): Jvalue() {

    override fun accept(v: Visitor) {
        v.visit(this)
        v.afterVisit(this)
    }

    override fun toString(): String {
        return value.toString()
    }
}

class Jbool (var value: Boolean): Jvalue() {

    override fun accept(v: Visitor) {
        v.visit(this)
        v.afterVisit(this)
    }

    override fun toString(): String {
        return value.toString()
    }
}

class Jnull: Jvalue() {

    override fun accept(v: Visitor) {
        v.visit(this)
        v.afterVisit(this)
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
    rootObj.addNode(Jnode("node06", Jbool(true)))
    rootObj.addNode(Jnode("node07", Jnull()))

    var rootNode1 = Jnode(value = rootObj)
    var rootNode2 = Jnode(value = rootArray)

    val jsonStr = rootNode1.toString()
    println("Stringifyed with toString" )
    println(jsonStr)

    val myStrVisitor = StringifyVisitor()

    rootNode1.accept(myStrVisitor)
    println("Stringifyed with Visitor" )
    println(myStrVisitor.str)

    val strVisitor = stringsVisitor()
    println("Find all strings with Visitor")
    rootNode1.accept(strVisitor)
    strVisitor.strs.forEach { println(it) }

    val findObjVisitor = findObjByNameVisitor("node04")
    println("Find objects by name with Visitor")
    rootNode1.accept(findObjVisitor)
    findObjVisitor.objs.forEach { println(it) }

}