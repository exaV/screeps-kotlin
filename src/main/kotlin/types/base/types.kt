package types.base

import types.base.global.BuildableStructureConstant
import types.base.prototypes.Owner
import types.base.prototypes.RoomObject
import types.extensions.lazyPerTick
import kotlin.js.Date

external interface JsDict<K, V>

@Suppress("NOTHING_TO_INLINE")
inline operator fun <K, V> JsDict<K, V>.get(key: K): V = asDynamic()[key] as V

val <K, V> JsDict<K, V>.keys: Array<K> by lazyPerTick {
    val keys = js("Object").keys(this) as? Array<K> ?: emptyArray()
    //println("creating iterator in tick ${Game.time} with keys=$keys")
    keys
}

class Entry<K, V>(override val key: K, override val value: V) : Map.Entry<K, V>

@Suppress("NOTHING_TO_INLINE")
inline operator fun <K, V> JsDict<K, V>.iterator(): Iterator<Map.Entry<K, V>> {
    return object : Iterator<Map.Entry<K, V>> {
        var currentIndex = 0

        override fun hasNext(): Boolean = currentIndex < keys.size

        override fun next(): Map.Entry<K, V> {
            val key = keys[currentIndex]
            currentIndex += 1
            val value = this@iterator.asDynamic()[key] as V
            return Entry(key, value)
        }
    }
}

external interface MutableJsDict<K, V> : JsDict<K, V>

@Suppress("NOTHING_TO_INLINE")
inline operator fun <K, V> MutableJsDict<K, V>.set(key: K, value: V) {
    asDynamic()[key] = value
}

class Filter(val filter: dynamic)


external class ConstructionSite : RoomObject {
    val my: Boolean
    val owner: Owner
    val progress: Number
    val progressTotal: Number
    val structureType: BuildableStructureConstant
    fun remove(): Number
}

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



