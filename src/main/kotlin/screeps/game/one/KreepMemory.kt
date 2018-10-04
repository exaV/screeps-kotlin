package screeps.game.one

import screeps.api.CreepMemory
import screeps.utils.memory.memory

var CreepMemory.state: CreepState by memory(CreepState.UNKNOWN)
var CreepMemory.targetId: String? by memory()
var CreepMemory.assignedEnergySource: String? by memory()
var CreepMemory.missionId: String? by memory()

enum class CreepState {
    UNKNOWN, IDLE, BUSY, REFILL, TRANSFERRING_ENERGY, CONSTRUCTING, UPGRADING, REPAIR, CLAIM, MISSION
}
