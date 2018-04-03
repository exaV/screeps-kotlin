package screeps.game.one.kreeps

import screeps.game.one.CreepState
import types.base.global.CreepMemory

class CreepSpawnOptions(state: CreepState) {
    val memory: CreepMemory = object : CreepMemory {
        val state: String = state.name
    }
}