package screeps.game.tutorials.tutorial2

import screeps.game.tutorials.tutorial1.Harvester
import types.Game
import types.StructureSpawn
import types.creepsMap

fun gameLoop() {
    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    val creeps = Game.creepsMap()

    for ((creepName, creep) in creeps) {
        val creepMemory = TutorialMemory(creep.memory)
        if (creepMemory.role == "harvester") {
            Harvester.run(creep, mainSpawn)
        }
        if (creepMemory.role == "upgrader") {
            Upgrader.run(creep);
        }
    }
}