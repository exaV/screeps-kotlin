package types

external class RoomPosition(x: Double, y: Double, name: String) {
    val x: Int
    val y: Int
    val name: String

    fun <T : RoomObject> findClosestByPath(
        type: FindConstant,
        objects: Array<RoomObject>,
        opts: dynamic = definedExternally
    ): T?

    fun <T : RoomObject> findClosestByPath(type: FindConstant, opts: dynamic = definedExternally): T?

    fun <T : RoomObject> findClosestByRange(
        type: FindConstant,
        objects: Array<RoomObject>,
        opts: dynamic = definedExternally
    ): T?

    fun <T : RoomObject> findClosestByRange(type: FindConstant, opts: dynamic = definedExternally): T?

    fun <T : RoomObject> findInRange(type: FindConstant, range: Int, opts: Filter = definedExternally): Array<T>

}

external interface RoomObject : GameObject {
    val pos: RoomPosition
    val room: Room
}

external interface Owner {
    val username: String
}

external interface Structure : RoomObject {
    val hits: Double
    val hitsMax: Double
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
    val spawning: Spawning?

    fun spawnCreep(body: Array<BodyType>, name: String): Number
    fun spawnCreep(body: Array<BodyType>, name: String, opts: dynamic): Number

}

external interface Spawning {
    val directions: dynamic
    val name: String
    val needTime: Number
    val remainingTime: Number

    fun cancel(): Number
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


