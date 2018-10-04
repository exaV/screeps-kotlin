package screeps.game.one

import screeps.api.CreepMemory

var CreepMemory.state: CreepState
    get() = try {
        CreepState.valueOf(this.asDynamic().state ?: CreepState.UNKNOWN.name)
    } catch (e: IllegalStateException) {
        CreepState.UNKNOWN
    }
    set(value) = run { this.asDynamic().state = value.name }

var CreepMemory.targetId: String?
    get() = this.asDynamic().targetId
    set(value) = run { this.asDynamic().targetId = value }

var CreepMemory.assignedEnergySource: String?
    get() = this.asDynamic().energysource
    set(value) = run { this.asDynamic().energysource = value }


var CreepMemory.missionId: String?
    get() = this.asDynamic().missionId
    set(value) = run { this.asDynamic().missionId = value }

enum class CreepState {
    UNKNOWN, IDLE, BUSY, REFILL, TRANSFERRING_ENERGY, CONSTRUCTING, UPGRADING, REPAIR, CLAIM, MISSION
}
