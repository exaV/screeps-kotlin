package screeps.game.tutorial3

import screeps.game.tutorial2.TutorialMemory
import types.*

fun gameLoop() {
    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    val creeps = Game.creepsMap()
    val rooms = Game.roomsMap()

    for ((roomName, room) in rooms) {
        if (room.memory.lastEnergy != room.energyAvailable) {
            console.log("Room $roomName has ${room.energyAvailable} energy available");
        }

        if (room.energyAvailable > 549) {
            mainSpawn.spawnCreep(arrayOf(WORK, WORK, WORK, WORK, CARRY, MOVE, MOVE), "HarvesterBig")
        }
    }

    for ((creepName, creep) in creeps) {
        val creepMemory = TutorialMemory(creep.memory)

        if (creepMemory.role == "harvester") {
            Harvester.run(creep)
        }
        if (creepMemory.role == "builder") {
            Builder.run(creep);
        }
    }

}