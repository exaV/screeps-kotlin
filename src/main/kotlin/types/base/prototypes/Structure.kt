package types.base.prototypes

import types.base.Filter
import types.base.ReservationDefinition
import types.base.SignDefinition
import types.base.global.BodyPartConstant
import types.base.global.FindConstant
import types.base.global.StructureConstant

external class RoomPosition(x: Int, y: Int, roomName: String) {
    val x: Int
    val y: Int
    val roomName: String

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

fun RoomPosition.copy(x: Int = this.x, y: Int = this.y, name: String = this.roomName) =
    RoomPosition(x, y, name)

open external class RoomObject : GameObject {
    val pos: RoomPosition
    val room: Room
}

external interface Owner {
    val username: String
}

open external class Structure : RoomObject {
    val hits: Double
    val hitsMax: Double
    val structureType: StructureConstant

    fun destroy(): Number
    fun isActive(): Boolean
    fun notifyWhenAttacked(enabled: Boolean): Number
}

open external class OwnedStructure : Structure {
    val my: Boolean
    val owner: Owner
}

external interface EnergyContainingStructure {
    val energy: Int
    val energyCapacity: Int
}

external class StructureSpawn : OwnedStructure, EnergyContainingStructure {
    val memory: dynamic
    val name: String

    val spawning: Spawning?

    fun spawnCreep(body: Array<BodyPartConstant>, name: String): Number
    fun spawnCreep(body: Array<BodyPartConstant>, name: String, opts: dynamic): Number

    override val energy: Int = definedExternally
    override val energyCapacity: Int = definedExternally
}

external interface Spawning {
    val directions: dynamic
    val name: String
    val needTime: Number
    val remainingTime: Number

    fun cancel(): Number
}


external class StructureController : OwnedStructure {
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
}


external class StructureTower : OwnedStructure, EnergyContainingStructure {
    fun attack(target: Creep): Number
    fun heal(target: Creep): Number
    fun repair(target: Structure): Number

    override val energy: Int = definedExternally
    override val energyCapacity: Int = definedExternally
}

external class StructureStorage : OwnedStructure {
    val store: Storage
    val storeCapacity: Int
}

