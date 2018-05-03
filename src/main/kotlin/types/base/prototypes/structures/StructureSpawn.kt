package types.base.prototypes.structures

import types.base.global.BodyPartConstant
import types.base.prototypes.OwnedStructure

external class StructureSpawn : OwnedStructure,
    EnergyContainingStructure {
    val memory: dynamic
    val name: String

    val spawning: Spawning?

    fun spawnCreep(body: Array<BodyPartConstant>, name: String): Number
    fun spawnCreep(body: Array<BodyPartConstant>, name: String, opts: dynamic): Number

    override val energy: Int = definedExternally
    override val energyCapacity: Int = definedExternally
}