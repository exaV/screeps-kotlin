package screeps.game.tutorial2

import types.CreepMemory


class TutorialMemory(val memory: CreepMemory) {
    val role: String? = memory.asDynamic().role
    var building: Boolean?
        get() = memory.asDynamic().building
        set(value) = run { memory.asDynamic().building = value }
}
