package types.base.prototypes.structures

import types.base.global.StructureConstant
import types.base.prototypes.*

fun RoomPosition.copy(x: Int = this.x, y: Int = this.y, name: String = this.roomName) =
    RoomPosition(x, y, name)

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

external interface EnergyContainingStructure {
    val energy: Int
    val energyCapacity: Int
}

external interface Spawning {
    val directions: dynamic
    val name: String
    val needTime: Number
    val remainingTime: Number

    fun cancel(): Number
}


external class StructureTower : OwnedStructure,
    EnergyContainingStructure {
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

