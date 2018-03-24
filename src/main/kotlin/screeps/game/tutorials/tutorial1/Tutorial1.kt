package screeps.game.tutorials.tutorial1

import types.*

fun gameLoop() {

    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    for ((_, creep: Creep) in Game.creepsMap()) {
        Harvester.run(creep, mainSpawn)
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