package gson_gen

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties

fun Number.toJnumber(): Jnumber {
    return Jnumber(this as Double)
}

fun String.toJstring(): Jstring {
    return Jstring(this)
}

fun Boolean.toJbool(): Jbool {
    return Jbool(this)
}


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
            .enumConstants.map { e -> e.toString().toJstring() } as MutableList<Jvalue>
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
    val direc = Direction::class.toJarray()
    val num = Numbers::class.toJarray()
    println(direc.toString())
    println(num.toString())
}