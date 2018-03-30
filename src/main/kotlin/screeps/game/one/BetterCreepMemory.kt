package screeps.game.one

import types.CreepMemory

class BetterCreepMemory(val memory: CreepMemory) {
    var state: CreepState
        get() = CreepState.valueOf(memory.asDynamic().state ?: CreepState.UNKNOWN.name)
        set(value) = run { memory.asDynamic().state = value.name }

    var targetId: String?
        get() = memory.asDynamic().targetId
        set(value) = run { memory.asDynamic().targetId = value }

    var assignedEnergySource: String?
        get() = memory.asDynamic().energysource
        set(value) = run { memory.asDynamic().energysource = value }

}

enum class CreepState {
    UNKNOWN, IDLE, BUSY, REFILL, TRANSFERRING_ENERGY, CONSTRUCTING, UPGRADING, REPAIR
}
