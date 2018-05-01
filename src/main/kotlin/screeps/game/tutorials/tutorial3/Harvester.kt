package screeps.game.tutorials.tutorial3

import types.*
import types.extensions.travelTo

object Harvester {
    fun run(creep: Creep) {
        if (creep.carry.energy < creep.carryCapacity) {
            val sources = creep.room.findEnergy();
            if (creep.harvest(sources[0]) == ERR_NOT_IN_RANGE) {
                creep.travelTo(sources[0].pos)
            }
        } else {
            val targets = creep.room.findStructures()
                .filter { (it.structureType == STRUCTURE_EXTENSION || it.structureType == STRUCTURE_SPAWN) }
                .map { (it as StructureSpawn) }
                .filter { it.energy < it.energyCapacity }

            if (targets.isNotEmpty()) {
                if (creep.transfer(targets[0], RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                    creep.travelTo(targets[0].pos);
                }
            }
        }
    }
}
