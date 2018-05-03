package screeps.game.one.kreeps

import screeps.game.one.CreepState
import types.base.global.CreepMemory
import types.base.prototypes.structures.SpawnOptions

class KreepSpawnOptions(state: CreepState) : SpawnOptions {
    override val memory = object : CreepMemory {
        val state: String = state.name
    }
}