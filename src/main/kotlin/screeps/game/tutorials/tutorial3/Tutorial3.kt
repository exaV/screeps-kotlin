package screeps.game.tutorials.tutorial3

import screeps.game.tutorials.tutorial2.TutorialMemory
import types.base.get
import types.base.global.CARRY
import types.base.global.Game
import types.base.global.MOVE
import types.base.global.WORK
import types.base.iterator
import types.base.prototypes.structures.StructureSpawn

fun gameLoop() {
    val mainSpawn: StructureSpawn = Game.spawns["Spawn1"]!!
    val creeps = Game.creeps
    val rooms = Game.rooms

    for ((roomName, room) in rooms) {
        if (room.memory.lastEnergy != room.energyAvailable) {
            console.log("Room $roomName has ${room.energyAvailable} energy available");
        }

        if (room.energyAvailable > 549) {
            mainSpawn.spawnCreep(
                arrayOf(
                    WORK,
                    WORK,
                    WORK,
                    WORK,
                    CARRY,
                    MOVE,
                    MOVE
                ), "HarvesterBig"
            )
        }
    }

    for ((_, creep) in creeps) {
        val creepMemory = TutorialMemory(creep.memory)

        if (creepMemory.role == "harvester") {
            Harvester.run(creep)
        }
        if (creepMemory.role == "builder") {
            Builder.run(creep);
        }
    }

}