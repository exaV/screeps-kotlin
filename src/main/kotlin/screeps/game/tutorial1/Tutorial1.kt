package screeps.game.tutorial1

import types.*

fun gameLoop() {

    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    for ((_, creep: Creep) in Game.creepsMap()) {


        if (creep.carry.energy < creep.carryCapacity) {
            val sources = creep.room.findEnergy()
            if (creep.harvest(sources[0]) == ERR_NOT_IN_RANGE) {
                creep.moveTo(sources[0].pos, VisualizePath());
            }
        } else if (mainSpawn.energy < mainSpawn.energyCapacity) {
            if (creep.transfer(mainSpawn, RESOURCE_ENERGY) == ERR_NOT_IN_RANGE) {
                creep.moveTo(mainSpawn.pos, VisualizePath());
            }
        }
    }

    if (Game.creepsMap().size < 2) {
        val body = arrayOf(WORK, CARRY, MOVE)
        val name = "Harvester_${Game.time}"
        val code = mainSpawn.spawnCreep(body, name);
        when (code) {
            OK -> console.log("spawning $name with body $body")
            ERR_BUSY -> console.log("busy")
            ERR_NOT_ENOUGH_ENERGY -> run { }
            else -> console.log("unhandled error code $code")
        }
    }


}