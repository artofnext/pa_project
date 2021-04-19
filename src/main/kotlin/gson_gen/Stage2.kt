package gson_gen

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberProperties


fun Number.toJvalue(): Jvalue {
    return Jnumber(this.toDouble())
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

    println(Direction.NORTH.toJvalue())
    val direc = Direction::class.toJarray()
    val num = Numbers::class.toJarray()
    println(direc.toString())
    println(num.toString())
    println(Numbers.THREE.number)

    println("String".toJvalue())
    println(256.toJvalue())
    println(true.toJvalue())
}


