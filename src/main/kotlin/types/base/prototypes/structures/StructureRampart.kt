package types.base.prototypes.structures

import types.base.global.ScreepsReturnCode
import types.base.prototypes.OwnedStructure

external class StructureRampart : OwnedStructure {
    val isPublic: Boolean
    val ticksToDecay: Int
    fun setPublic(isPublic: Boolean): ScreepsReturnCode
}