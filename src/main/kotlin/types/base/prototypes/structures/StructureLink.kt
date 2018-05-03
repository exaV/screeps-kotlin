package types.base.prototypes.structures

import types.base.prototypes.OwnedStructure

abstract external class StructureLink : OwnedStructure, EnergyContainingStructure {
    val cooldown: Int
    fun transferEnergy(target: StructureLink, amount: Int = definedExternally)
}