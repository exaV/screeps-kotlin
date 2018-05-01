package screeps.game.tutorials.tutorial1

import types.base.get
import types.base.global.*
import types.base.prototypes.Creep
import types.base.prototypes.StructureSpawn
import types.base.toMap

fun gameLoop() {

    val creeps = Game.creeps.toMap()
    val mainSpawn: StructureSpawn = Game.spawns["Spawn1"]!!
    for ((_, creep: Creep) in creeps) {
        Harvester.run(creep, mainSpawn)
    }

    if (creeps.size < 2) {
        val body = arrayOf<BodyPartConstant>(WORK, CARRY, MOVE)
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