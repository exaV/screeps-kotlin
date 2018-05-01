package screeps.game.tutorials.tutorial3

import screeps.game.tutorials.tutorial2.TutorialMemory
import types.base.global.ERR_NOT_IN_RANGE
import types.base.prototypes.Creep
import types.base.prototypes.findConstructionSites
import types.base.prototypes.findEnergy
import types.extensions.travelTo

object Builder {
    fun run(creep: Creep) {
        val creepMemory = TutorialMemory(creep.memory)

        if (creepMemory.building == true && creep.carry.energy == 0) {
            creepMemory.building = false;
            creep.say("🔄 harvest")
        }
        if (creepMemory.building != true && creep.carry.energy == creep.carryCapacity) {
            creepMemory.building = true;
            creep.say("🚧 build");
        }

        if (creepMemory.building == true) {
            val targets = creep.room.findConstructionSites()
            if (targets.isNotEmpty()) {
                if (creep.build(targets[0]) == ERR_NOT_IN_RANGE) {
                    creep.travelTo(targets[0].pos);
                }
            }
        } else {
            val sources = creep.room.findEnergy()
            if (creep.harvest(sources[0]) == ERR_NOT_IN_RANGE) {
                creep.travelTo(sources[0].pos)
            }
        }
    }
}
