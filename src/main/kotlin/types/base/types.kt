package types.base

import types.base.global.StringConstant
import kotlin.js.Date

external interface JsDict<V>

@Suppress("NOTHING_TO_INLINE")
inline operator fun <V> JsDict<V>.get(key: String): V? = asDynamic()[key] as? V

inline operator fun <V> JsDict<V>.get(key: StringConstant): V? = asDynamic()[key] as? V

val <V> JsDict<V>.keys: Array<String>
    get() = js("Object").keys(this) as? Array<String> ?: emptyArray()

class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>

@Suppress("NOTHING_TO_INLINE")
inline operator fun <V> JsDict<V>.iterator(): Iterator<Map.Entry<String, V>> {
    return object : Iterator<Map.Entry<String, V>> {
        var currentIndex = 0

        override fun hasNext(): Boolean = currentIndex < keys.size

        override fun next(): Map.Entry<String, V> {
            val key = keys[currentIndex]
            currentIndex += 1
            val value = this@iterator.asDynamic()[key] as V
            return Entry(key, value)
        }
    }
}

fun <V> JsDict<V>.toMap(): Map<String, V> {
    val map: MutableMap<String, V> = linkedMapOf()
    for (key in keys) {
        val value: V? = this[key]
        if (value != null) {
            map[key] = value
        } else {
            println("null value for key $key")
        }
    }
    return map
}

external interface MutableJsDict<V> : JsDict<V>

@Suppress("NOTHING_TO_INLINE")
inline operator fun <V> MutableJsDict<V>.set(key: String, value: V) {
    asDynamic()[key] = value
}

class Filter(val filter: dynamic)


external interface ReservationDefinition {
    var username: String
    var ticksToEnd: Number
}

external interface SignDefinition {
    var username: String
    var text: String
    var time: Number
    var datetime: Date
}



