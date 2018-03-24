package screeps.game.tutorial2

import types.CreepMemory


interface TutorialMemory : CreepMemory {
    var role: String?
    var building: Boolean?
}