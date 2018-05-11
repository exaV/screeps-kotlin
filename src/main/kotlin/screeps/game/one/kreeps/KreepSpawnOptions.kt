package screeps.game.one.kreeps

import screeps.game.one.CreepState
import screeps.game.one.missionId
import screeps.game.one.state
import types.base.global.CreepMemory
import types.base.prototypes.structures.SpawnOptions

class KreepSpawnOptions(state: CreepState, missionId: String? = null) : SpawnOptions {
    override val memory = object : CreepMemory {}

    init {
        memory.state = state
        memory.missionId = missionId
    }
}