package screeps.game.tutorials.tutorial1

import types.base.global.ERR_NOT_IN_RANGE
import types.base.global.RESOURCE_ENERGY
import types.base.prototypes.Creep
import types.base.prototypes.findEnergy
import types.base.prototypes.structures.StructureSpawn
import types.extensions.travelTo

object Harvester {
    fun run(creep: Creep, spawn: StructureSpawn) {
        if (creep.carry.energy < creep.carryCapacity) {
            val sources = creep.room.findEnergy()
            if (creep.harvest(sources[0]) == ERR_NOT_IN_RANGE) {
                creep.travelTo(sources[0].pos)
            }
        } else if (spawn.energy < spawn.energyCapacity) {
            if (creep.transfer(spawn, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.travelTo(spawn.pos)
            }
        }
    }
}