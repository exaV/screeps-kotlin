package screeps.game.tutorial4

import screeps.game.tutorial2.TutorialMemory
import screeps.game.tutorial3.Builder
import screeps.game.tutorial3.Harvester
import types.*
import types.base.global.Memory

external fun delete(p: dynamic): Boolean = definedExternally
//...

enum class Role {
    HARVESTER,
    BUILDER,
    UPGRADER
}

class CreepOptions(role: Role) {
    val memory: CreepMemory = object : CreepMemory {
        val role: String = role.name.toLowerCase()
    }
}

fun gameLoop() {
    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    val creeps = Game.creepsMap()
    val rooms = Game.roomsMap()

    //housekeeping
    for ((creepName, _) in jsonToMap<Creep>(Memory.creeps)) {
        if (creeps[creepName] == null) {
            console.log("deleting obselete memory entry for creep $creepName")
            delete(Memory.creeps[creepName])
        }
    }

    val harvesters =
        creeps.filter { (_, creep) -> TutorialMemory(creep.memory).role == Role.HARVESTER.name.toLowerCase() }
    if (harvesters.size < 2) {
        val newName = "Harvester_${Game.time}"
        if (mainSpawn.spawnCreep(arrayOf(WORK, CARRY, MOVE), newName, CreepOptions(Role.HARVESTER)) == OK) {
            console.log("spawning $newName")
        }
    }

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