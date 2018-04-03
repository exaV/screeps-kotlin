package screeps.game.tutorials.tutorial2

import types.base.global.CreepMemory


class TutorialMemory(val memory: CreepMemory) {
    val role: String? = memory.asDynamic().role
    var building: Boolean?
        get() = memory.asDynamic().building
        set(value) = run { memory.asDynamic().building = value }
}
