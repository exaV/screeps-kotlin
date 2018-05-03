package types.base.prototypes

import types.base.global.ResourceConstant
import types.base.global.ScreepsReturnCode

external class StructureTerminal : OwnedStructure {
    val cooldown: Int
    val store: Storage
    val storeCapacity: Int
    fun send(
        resourceType: ResourceConstant,
        amount: Int,
        destination: String,
        description: String = definedExternally
    ): ScreepsReturnCode
}