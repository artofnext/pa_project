import gson_generator.*

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