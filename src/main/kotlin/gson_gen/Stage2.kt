package gson_gen

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation


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
        else -> dataClassToJobject(value)
    }
}

// Annotation class to exclude property from data model
@Target(AnnotationTarget.PROPERTY)
annotation class Remove()

// Annotation class to rename property from data model
// accepts String as new property name
@Target(AnnotationTarget.PROPERTY)
annotation class Rename(val newName: String)

// Function generate data model from data class object
// accepts data class Object
// returns Jobject
fun dataClassToJobject(obj: Any): Jvalue {
    val clazz: KClass<Any> = obj::class as KClass<Any>
    val resultList = mutableListOf<Jnode>()
    if(!clazz.isData) {
//        println("Is not a data class")
        return Jstring("is not a data class")
    } else {

        clazz.declaredMemberProperties.forEach {
            val propVal = it.call(obj)

            // todo there should be better solution
            if (it.hasAnnotation<Remove>()) {
                // I just too tired to be reasonable
            }else if(it.hasAnnotation<Rename>()) {
                resultList.add(Jnode(
                    it.findAnnotation<Rename>()!!.newName,
                    getJvalue(propVal)
                ))
            } else {
                resultList.add(Jnode(it.name, getJvalue(propVal)))
            }
        }
    return Jobject(resultList)
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

    val JSONVisitor = StringifyVisitor()

    val jUser1 = dataClassToJobject(user1)
    jUser1.accept(JSONVisitor)
    println(jUser1)
    println(JSONVisitor.str)
    println(dataClassToJobject(person1))
}


