package screeps.game.tutorials.tutorial2

import types.Creep
import types.ERR_NOT_IN_RANGE
import types.extensions.travelTo
import types.findEnergy

object Upgrader {

    fun run(creep: Creep) {
        if (creep.carry.energy == 0) {
            val sources = creep.room.findEnergy()
            if (creep.harvest(sources[0]) == ERR_NOT_IN_RANGE) {
                creep.travelTo(sources[0].pos);
            }
        } else {
            creep.room.controller?.let {
                if (creep.upgradeController(it) == ERR_NOT_IN_RANGE) {
                    creep.travelTo(it.pos);
                }
            }
        }
    }
}
