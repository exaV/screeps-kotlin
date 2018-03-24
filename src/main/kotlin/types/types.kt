package types

import kotlin.js.Date
import kotlin.js.Json

external interface Room {
    val prototype: Room
    val energyAvailable: Double
    val energyCapacityAvailable: Double
    val memory: dynamic
    val mode: String
    val name: String
    val controller: StructureController?
    fun createConstructionSite(x: Number, y: Number, structureType: dynamic): dynamic
    fun createConstructionSite(pos: RoomPosition, structureType: dynamic): dynamic
    fun createFlag(
        pos: RoomPosition,
        name: String? = definedExternally,
        color: dynamic = definedExternally,
        secondaryColor: dynamic = definedExternally
    ): dynamic

    fun findExitTo(room: String): dynamic
    fun findExitTo(room: Room): dynamic
    fun getPositionAt(x: Number, y: Number): RoomPosition?
    fun lookAt(x: Number, y: Number): Array<Any? /* Any? & `T$79` */>
    fun lookAt(target: RoomPosition): Array<Any? /* Any? & `T$79` */>
    fun lookAtArea(
        top: Number,
        left: Number,
        bottom: Number,
        right: Number,
        asArray: Boolean? = definedExternally /* null */
    ): dynamic /* LookAtResultMatrix<dynamic /* String /* "creep" */ | String /* "source" */ | String /* "energy" */ | String /* "resource" */ | String /* "mineral" */ | String /* "structure" */ | String /* "flag" */ | String /* "constructionSite" */ | String /* "nuke" */ | String /* "terrain" */ */> | Array<Any? /* Any? & `T$79` & `T$95` */> */

    fun find(FIND_CONSTANT: Number): Array<RoomObject>
    fun find(FIND_CONSTANT: Number, opts: dynamic): Array<RoomObject>
}

@Suppress("UNCHECKED_CAST")
fun Room.findCreeps() = find(FIND_CREEPS) as Array<Creep>

@Suppress("UNCHECKED_CAST")
fun Room.findEnergy() = find(FIND_SOURCES) as Array<Source>

@Suppress("UNCHECKED_CAST")
fun Room.findConstructionSites() = find(FIND_CONSTRUCTION_SITES) as Array<ConstructionSite>

@Suppress("UNCHECKED_CAST")
fun Room.findStructures() = find(FIND_STRUCTURES) as Array<Structure>

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

external object Game {
    var cpu: CPU
    var creeps: Json = definedExternally
    var gcl: GlobalControlLevel
    var resources: Json
    var rooms: Json
    var spawns: Json
    var structures: StructuresMap
    var constructionSites: Json
    var shard: Shard
    var time: Number
    fun <T> getObjectById(id: String?): T?
    fun notify(message: String, groupInterval: Number? = definedExternally /* null */): Nothing?
}

fun Game.creepsMap(): Map<String, Creep> = jsonToMap(creeps)
fun Game.spawnsMap(): Map<String, StructureSpawn> = jsonToMap(spawns)
fun Game.roomsMap(): Map<String, Room> = jsonToMap(rooms)
fun Game.constructionsSitesMap(): Map<String, ConstructionSite> = jsonToMap(constructionSites)

external interface Shard {
    var name: String
    var type: String /* "normal" */
    var ptr: Boolean
}

external interface ConstructionSite : RoomObject {
    var id: String
    var my: Boolean
    var owner: Owner
    var progress: Number
    var progressTotal: Number
    var structureType: String
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

external interface CreepMemory
external interface FlagMemory
external interface RoomMemory
external interface SpawnMemory
external interface CreepMap : TMap<Creep>
external interface RoomMap : TMap<Room>
external interface SpawnsMap : TMap<StructureSpawn>
external interface StructuresMap : TMap<Structure>
external interface ConstructionSiteMap : TMap<ConstructionSite>
external interface TMap<T> {

    val length: Number?
}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> TMap<T>.get(name: String): T? = asDynamic()[name]

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T> TMap<T>.set(roomName: String, value: T) {
    asDynamic()[roomName] = value
}


