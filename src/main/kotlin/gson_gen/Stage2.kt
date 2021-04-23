package gson_gen

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
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
            e -> listList.add(getJvalue(e))
    }
    return Jarray(listList)
}

fun <E> Set<E>.toJvalue(): Jvalue {
    val setList = mutableListOf<Jvalue>()

    this.forEach {
            e -> setList.add(getJvalue(e))
    }
    return Jarray(setList)
}

fun <K, V > Map<K, V>.toJvalue(): Jvalue {
    val mapList = mutableListOf<Jnode>()
    this.forEach {
            (k, v) -> mapList.add(Jnode(k.toString(), getJvalue(v)))
    }
    return Jobject(mapList)
}

fun getJvalue(value: Any?): Jvalue {
    return when (value) {
        is Number -> Jnumber(value)
        is String -> Jstring(value)
        is Boolean -> Jbool(value)
        null -> Jnull()
        is Set<Any?> -> value.toJvalue()
        is Map<*, *> -> value.toJvalue()
        is Enum<*> -> Jstring(value.toString())
        else -> dataClassToJnode(value)
    }
}

@Target(AnnotationTarget.PROPERTY)
annotation class Remove()

@Target(AnnotationTarget.PROPERTY)
annotation class Rename(val newName: String)

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

            if (it.hasAnnotation<Remove>()) {
                // I just too tired to be reasonable троха задовбавсі
            }else if(it.hasAnnotation<Rename>()) {
                resultList.add(Jnode(
                    it.findAnnotation<Rename>()!!.newName,
                    getJvalue(propVal)
                ))
            } else {
                resultList.add(Jnode(it.name, getJvalue(propVal)))
            }
        }
    return Jnode(nodeName, Jobject(resultList))
    }
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

fun main () {

    val person1: Person = Person("Mary", 23)
    val props2: Props = Props("approved", person1)
    val props1: Props = Props("approved", props2)
    val user1 = User("Alex", 22, props1)

    println(dataClassToJnode(user1))
    println(dataClassToJnode(person1))
}


