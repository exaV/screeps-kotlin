package screeps.game.tutorials.tutorial2

import screeps.game.tutorials.tutorial1.Harvester
import types.base.get
import types.base.global.Game
import types.base.iterator
import types.base.prototypes.structures.StructureSpawn

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns["Spawn1"]!!
    val creeps = Game.creeps

    for ((_, creep) in creeps) {
        val creepMemory = TutorialMemory(creep.memory)
        if (creepMemory.role == "harvester") {
            Harvester.run(creep, mainSpawn)
        }
        if (creepMemory.role == "upgrader") {
            Upgrader.run(creep);
        }
    }
}