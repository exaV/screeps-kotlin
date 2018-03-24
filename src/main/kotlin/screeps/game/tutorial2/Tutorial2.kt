package screeps.game.tutorial2

import screeps.game.tutorial1.Harvester
import types.Game
import types.StructureSpawn
import types.creepsMap

fun gameLoop() {
    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    val creeps = Game.creepsMap()

    for ((creepName, creep) in creeps) {
        if (creep.memory.role == "harvester") {
            Harvester.run(creep, mainSpawn)
        }
        if (creep.memory.role == "upgrader") {
            Upgrader.run(creep);
        }
    }
}