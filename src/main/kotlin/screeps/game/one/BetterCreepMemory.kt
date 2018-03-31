package screeps.game.one

import types.CreepMemory

var CreepMemory.state: CreepState
    get() = CreepState.valueOf(this.asDynamic().state ?: CreepState.UNKNOWN.name)
    set(value) = run { this.asDynamic().state = value.name }

var CreepMemory.targetId: String?
    get() = this.asDynamic().targetId
    set(value) = run { this.asDynamic().targetId = value }

var CreepMemory.assignedEnergySource: String?
    get() = this.asDynamic().energysource
    set(value) = run { this.asDynamic().energysource = value }

enum class CreepState {
    UNKNOWN, IDLE, BUSY, REFILL, TRANSFERRING_ENERGY, CONSTRUCTING, UPGRADING, REPAIR
}
