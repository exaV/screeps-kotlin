package types.base.global

import kotlin.js.Json

external object Memory {
    var creeps: Json?
    var flags: Json
    var rooms: Json
    var spawns: Json

}

@Suppress("NOTHING_TO_INLINE")
inline operator fun Memory.get(name: String): Any? = asDynamic()[name]

@Suppress("NOTHING_TO_INLINE")
inline operator fun Memory.set(name: String, value: Any) {
    asDynamic()[name] = value
}