package types

external class RoomPosition(x: Double, y: Double, name: String) {
    val x: Double
    val y: Double
    val name: String
}

external interface RoomObject {
    val pos: RoomPosition
    val room: Room
}

external interface Owner {
    val username: String
}

external interface Structure : RoomObject {
    val hits: Double
    val hitsMax: Double
    val id: String
    val structureType: String

    fun destroy(): Number
    fun isActive(): Boolean
    fun notifyWhenAttacked(enabled: Boolean): Number
}

external interface OwnedStructure : Structure {
    val my: Boolean
    val owner: Owner
}

external interface StructureSpawn : OwnedStructure {
    val energy: Double
    val energyCapacity: Double
    val memory: dynamic
    val name: String
    val spawning: dynamic

    fun spawnCreep(body: Array<String>, name: String): Number
    fun spawnCreep(body: Array<BodyType>, name: String): Number
    fun spawnCreep(body: Array<BodyType>, name: String, ops: dynamic): Number

}


external interface StructureController : OwnedStructure {
    val level: Number
    val progress: Number
    val progressTotal: Number
    val reservation: ReservationDefinition?
    val safeModeAvailable: Number
    val sign: SignDefinition?
    val ticksToDowngrade: Number
    val upgradeBlocked: Number
    var safeModeCooldown: Number? get() = definedExternally; set(value) = definedExternally
    var safeMode: Number? get() = definedExternally; set(value) = definedExternally
    fun activateSafeMode(): dynamic
    fun unclaim(): dynamic

}

external interface BodyType
external object WORK : BodyType
external object CARRY : BodyType
external object MOVE : BodyType
