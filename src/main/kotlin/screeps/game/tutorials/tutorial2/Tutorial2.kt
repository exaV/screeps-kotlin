package screeps.game.tutorials.tutorial2

import screeps.game.tutorials.tutorial1.Harvester
import types.Creep
import types.StructureSpawn
import types.base.global.Game
import types.jsonToMap

fun gameLoop() {
    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    val creeps = jsonToMap<Creep>(Game.creeps)

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