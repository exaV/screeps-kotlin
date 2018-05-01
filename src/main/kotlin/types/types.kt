package types

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
external interface CPUShardLimits

@Suppress("NOTHING_TO_INLINE")
inline operator fun CPUShardLimits.get(shard: String): Number? = asDynamic()[shard]

@Suppress("NOTHING_TO_INLINE")
inline operator fun CPUShardLimits.set(shard: String, value: Number) {
    asDynamic()[shard] = value
}

external interface CPU {
    var limit: Int
    var tickLimit: Int
    var bucket: Int
    var shardLimits: CPUShardLimits
    fun getUsed(): Number
    fun setShardLimits(limits: CPUShardLimits): dynamic /* Number /* 0 */ | Number /* -4 */ | Number /* -10 */ */
}

external interface GlobalControlLevel {
    var level: Number
    var progress: Number
    var progressTotal: Number
}

external interface Shard {
    var name: String
    var type: String /* "normal" */
    var ptr: Boolean
}


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



