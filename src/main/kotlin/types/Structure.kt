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

fun RoomPosition.copy(x: Int = this.x, y: Int = this.y, name: String = this.name) = RoomPosition(x.toDouble(), y.toDouble(), name)

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

external class StructureContainer : Structure {
    val store: Storage
    val storeCapacity: Int
    val ticksToDecay: Int

    override val pos: RoomPosition
    override val room: Room
    override val hits: Double
    override val hitsMax: Double
    override val structureType: String
    override fun destroy(): Number
    override fun isActive(): Boolean
    override fun notifyWhenAttacked(enabled: Boolean): Number
    override val id: String
}


external class StructureTower : OwnedStructure {
    val energy: Int
    val energyCapacity: Int
    fun attack(target: Creep): Number
    fun heal(target: Creep): Number
    fun repair(target: Structure): Number


    override val pos: RoomPosition
    override val room: Room
    override val hits: Double
    override val hitsMax: Double
    override val structureType: String
    override fun destroy(): Number
    override fun isActive(): Boolean
    override fun notifyWhenAttacked(enabled: Boolean): Number
    override val my: Boolean
    override val owner: Owner
    override val id: String
}

