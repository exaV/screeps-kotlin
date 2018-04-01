package types

import kotlin.js.Date
import kotlin.js.Json

external interface Room {
    val prototype: Room
    val energyAvailable: Int
    val energyCapacityAvailable: Int
    val memory: dynamic
    val mode: String
    val name: String
    val controller: StructureController?
    fun createConstructionSite(x: Number, y: Number, structureType: StructureConstant): Number
    fun createConstructionSite(pos: RoomPosition, structureType: StructureConstant): Number
    fun createFlag(
        pos: RoomPosition,
        name: String? = definedExternally,
        color: dynamic = definedExternally,
        secondaryColor: dynamic = definedExternally
    ): dynamic

    fun findExitTo(room: String): dynamic
    fun findExitTo(room: Room): dynamic
    fun getPositionAt(x: Number, y: Number): RoomPosition?
    fun lookAt(x: Number, y: Number): Array<LookAt>
    fun lookAt(target: RoomPosition): Array<LookAt>
    fun lookAtArea(
        top: Number,
        left: Number,
        bottom: Number,
        right: Number,
        asArray: Boolean? = definedExternally /* null */
    ): dynamic /* LookAtResultMatrix<dynamic /* String /* "creep" */ | String /* "source" */ | String /* "energy" */ | String /* "resource" */ | String /* "mineral" */ | String /* "structure" */ | String /* "flag" */ | String /* "constructionSite" */ | String /* "nuke" */ | String /* "terrain" */ */> | Array<Any? /* Any? & `T$79` & `T$95` */> */

    fun findPath(fromPos: RoomPosition, toPos: RoomPosition, opts: FindPathOpts? = definedExternally): Array<PathStep>

    fun <T : RoomObject> find(FIND_CONSTANT: Number): Array<T>
    fun <T : RoomObject> find(FIND_CONSTANT: Number, opts: Filter): Array<T>
}

external class LookAt {
    val type: LookConstant
    val creep: Creep?
    val structure: Structure?
    val terrain: String?
    val constructionSite: ConstructionSite?
    val resource: Resource?
}

class Filter(val filter: dynamic)

open class FindPathOpts(
    val ignoreCreeps: Boolean = false,
    val ignoreDestructibleStructures: Boolean = false,
    val ignoreRoads: Boolean = false,
    val costCallback: dynamic = null,
    val maxOps: Int = 2000,
    val heuristicWeight: Double = 1.2,
    val serialize: Boolean = false,
    val maxRooms: Int = 16,
    val range: Int = 0,
    val plainCost: Double = 1.0,
    val swampCost: Double = 5.0
)

external interface PathStep {
    var x: Int
    var dx: Int
    var y: Int
    var dy: Int
    var direction: dynamic /* Number /* 1 */ | Number /* 2 */ | Number /* 3 */ | Number /* 4 */ | Number /* 5 */ | Number /* 6 */ | Number /* 7 */ | Number /* 8 */ */
}

fun Room.findCreeps() = find<Creep>(FIND_CREEPS)
fun Room.findEnergy() = find<Source>(FIND_SOURCES)
fun Room.findConstructionSites() = find<ConstructionSite>(FIND_CONSTRUCTION_SITES)
fun Room.findStructures() = find<Structure>(FIND_STRUCTURES)
fun Room.findDroppedEnergy() = find<Resource>(FIND_DROPPED_RESOURCES, Filter("energy"))


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
    val cpu: CPU
    val creeps: Json = definedExternally
    val gcl: GlobalControlLevel
    val resources: Json
    val rooms: Json
    val spawns: Json
    val structures: Json
    val constructionSites: Json
    val shard: Shard
    val time: Number
    fun <T : GameObject> getObjectById(id: String?): T?
    fun notify(message: String, groupInterval: Number? = definedExternally /* null */): Nothing?
}

fun Game.roomsMap(): Map<String, Room> = jsonToMap(rooms)

external interface Shard {
    var name: String
    var type: String /* "normal" */
    var ptr: Boolean
}

external class ConstructionSite : RoomObject {
    var my: Boolean
    var owner: Owner
    var progress: Number
    var progressTotal: Number
    var structureType: String
    fun remove(): Number

    override val pos: RoomPosition
    override val room: Room
    override val id: String
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


