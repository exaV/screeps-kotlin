package types.base.prototypes.structures

import types.base.global.StructureConstant
import types.base.prototypes.RoomObject
import types.base.prototypes.RoomPosition

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

/**
 * Energy can be used for spawning of creeps.
 */
external interface EnergyStructure : EnergyContainingStructure

external interface DecayingStructure {
    val ticksToDecay: Int
}




