package types

external class RoomPosition(x: Double, y: Double, name: String) {
    val x: Double
    val y: Double
    val name: String

    fun <T : RoomObject> findClosestByPath(type: FindConstant, objects: Array<RoomObject>, opts: dynamic = definedExternally): T?
    fun <T : RoomObject> findClosestByPath(type: FindConstant, opts: dynamic = definedExternally): T?

    fun <T : RoomObject> findClosestByRange(type: FindConstant, objects: Array<RoomObject>, opts: dynamic = definedExternally): T?
    fun <T : RoomObject> findClosestByRange(type: FindConstant, opts: dynamic = definedExternally): T?

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
    val energy: Int
    val energyCapacity: Int
    val memory: dynamic
    val name: String
    val spawning: dynamic

    fun spawnCreep(body: Array<BodyType>, name: String): Number
    fun spawnCreep(body: Array<BodyType>, name: String, ops: dynamic): Number
    fun spawnCreep(body: List<BodyType>, name: String, ops: dynamic): Number

}


external interface StructureController : OwnedStructure {
    val level: Int
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


