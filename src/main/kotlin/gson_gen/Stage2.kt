package gson_gen

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties


fun Number.toJvalue(): Jvalue {
    return Jnumber(this.toDouble())
}

fun Double.toJvalue(): Jvalue {
    return Jnumber(this)
}

fun String.toJvalue(): Jvalue {
    return Jstring(this)
}

fun Boolean.toJvalue(): Jvalue {
    return Jbool(this)
}

fun Direction.toJvalue(): Jvalue {
    return Jstring(this.toString())
}

fun <E> List<E>.toJvalue(): Jvalue {
    val listList = mutableListOf<Jvalue>()
    this.forEach {
            e -> listList.add(e.toString().toJvalue())
    }
    return Jarray(listList)
}

fun <E> Set<E>.toJvalue(): Jvalue {
    val setList = mutableListOf<Jvalue>()
    this.forEach {
            e -> setList.add(e.toString().toJvalue())
    }
    return Jarray(setList)
}

fun <K, V > Map<K, V>.toJvalue(): Jvalue {
    val mapList = mutableListOf<Jnode>()
    this.forEach {
            (k, v) -> mapList.add(Jnode(k.toString(), v.toString().toJvalue()))
    }
    return Jobject(mapList)
}

// recursive function
fun dataClassToJnode(obj: Any, nodeName: String = "root"): Jnode {
    val clazz: KClass<Any> = obj::class as KClass<Any>
    val resultList = mutableListOf<Jnode>()
    if(!clazz.isData) {
//        println("Is not a data class")
        return Jnode(clazz.simpleName.toString(), Jstring("is not a data class"))
    } else {

        clazz.declaredMemberProperties.forEach {
            val propVal = it.call(obj)
            if(propVal == null) { resultList.add(Jnode(it.name, Jnull())) }
            else if(propVal is String) {
                resultList.add(Jnode(it.name, propVal.toString().toJvalue()))
            } else if(propVal is Number) {
                resultList.add(Jnode(it.name, propVal.toDouble().toJvalue()))
            } else if(propVal is Boolean) {
                resultList.add(Jnode(it.name, propVal.toJvalue()))
            } else {
                //if(propVal::class.isData) {
                    resultList.add(dataClassToJnode(propVal, it.name))
                //}
            }
//            println(it.name + ": " + it.returnType + " = " + it.call(obj).toString())
        }
    return Jnode(nodeName, Jobject(resultList))
    }
//    println(clazz.qualifiedName)
//    println(clazz.simpleName)
//    println(clazz.isData)
//    println(clazz.annotations)
//    println(clazz.declaredMemberProperties)

}

// recursive function
fun dataClassToJnode1(obj: Any, nodeName: String = "root"): Jnode {
    val clazz: KClass<Any> = obj::class as KClass<Any>
    val resultList = mutableListOf<Jnode>()
    if(!clazz.isData) {
//        println("Is not a data class")
        return Jnode(clazz.simpleName.toString(), Jstring("is not a data class"))
    } else {

        clazz.declaredMemberProperties.forEach {
            val propVal = it.call(obj)
            if(propVal == null) { resultList.add(Jnode(it.name, Jnull())) }
            else if(propVal is String || propVal is Number || propVal is Boolean) {
                resultList.add(Jnode(it.name, propVal.toString().toJvalue()))
            } else {
                resultList.add(dataClassToJnode(propVal, it.name))
            }
        }
        return Jnode(nodeName, Jobject(resultList))
    }
}

// test classes

data class User(val name: String, val age: Int, val props: Any?) {
    val sity = null
    val number = 10
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

var i = Direction.values()

// usage: Direction::class.toJarray()
// is there a simple way to get all Enum values as string?
fun <T : Enum<*>> KClass<T>.toJarray(): Jarray {
    return Jarray(
        Class.forName(this.qualifiedName)
            .enumConstants.map { e -> e.toString().toJvalue() } as MutableList<Jvalue>
    )
}

//fun <T : Enum<*>> KClass<T>.join(skipFirst: Int = 0, skipLast: Int = 0): String {
//    return this.declaredMemberProperties
//        .drop(skipFirst)
//        .dropLast(skipLast)
//        .map { e -> e.name }
//        .joinToString()
//}

fun main () {

//    println(Direction.NORTH.toJvalue())
//    val direc = Direction::class.toJarray()
//    val num = Numbers::class.toJarray()
//    println(direc.toString())
//    println(num.toString())
//    println(Numbers.THREE.number)
//
//    println("String".toJvalue())
//    println(256.toJvalue())
//    println(true.toJvalue())
//
//    val myMap = mapOf<String, String>("name1" to "val1", "name2" to "val2")
//    println(myMap.toJvalue())

    val person1: Person = Person("Mary", 23)
    val props2: Props = Props("approved", person1)
    val props1: Props = Props("approved", props2)
    val user1 = User("Alex", 22, props1)

    println(dataClassToJnode(user1))
    println(dataClassToJnode(person1))



}


