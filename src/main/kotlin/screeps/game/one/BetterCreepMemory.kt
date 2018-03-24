package screeps.game.one

import types.CreepMemory

class BetterCreepMemory(val memory: CreepMemory) {
    var state: CreepState
        get() = CreepState.valueOf(memory.asDynamic().state ?: CreepState.UNKNOWN.name)
        set(value) = run { memory.asDynamic().state = value.name }

    var building: String?
        get() = memory.asDynamic().building
        set(value) = run { memory.asDynamic().building = value }

    var upgrading: String?
        get() = memory.asDynamic().upgrading
        set(value) = run { memory.asDynamic().upgrading = value }

}

enum class CreepState {
    UNKNOWN, IDLE, BUSY, REFILL, TRANSFERRING_ENERGY, CONSTRUCTING, UPGRADING
}
