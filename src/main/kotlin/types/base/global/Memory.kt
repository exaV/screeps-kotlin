package types.base.global

import types.base.MutableJsDict

//TODO the object should probably be empty since all the contents are user defined
external object Memory {
    var creeps: MutableJsDict<CreepMemory>
    var flags: MutableJsDict<FlagMemory>?
    var rooms: MutableJsDict<RoomMemory>
    var spawns: MutableJsDict<SpawnMemory>?

}

@Suppress("NOTHING_TO_INLINE")
inline operator fun Memory.get(name: String): Any? = asDynamic()[name]

@Suppress("NOTHING_TO_INLINE")
inline operator fun Memory.set(name: String, value: Any) {
    asDynamic()[name] = value
}

external interface CreepMemory
external interface FlagMemory
external interface RoomMemory
external interface SpawnMemory
