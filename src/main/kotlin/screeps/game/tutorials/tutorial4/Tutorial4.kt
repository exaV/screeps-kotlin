package screeps.game.tutorials.tutorial4

import screeps.game.tutorials.tutorial2.TutorialMemory
import screeps.game.tutorials.tutorial2.Upgrader
import screeps.game.tutorials.tutorial3.Builder
import screeps.game.tutorials.tutorial3.Harvester
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

val minPopulations = arrayOf(Role.HARVESTER to 2, Role.UPGRADER to 1, Role.BUILDER to 2)

fun gameLoop() {
    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    val creeps = jsonToMap<Creep>(Game.creeps)
    val rooms = Game.roomsMap()

    //delete memories of creeps that have passed away
    houseKeeping(creeps)

    //make sure we have at least some creeps
    populationControl(minPopulations, creeps, mainSpawn)


    for ((roomName, room) in rooms) {
        if (room.memory.lastEnergy != room.energyAvailable) {
            console.log("Room $roomName has ${room.energyAvailable} energy available");
        }

        if (room.energyAvailable > 549) {
            mainSpawn.spawnCreep(
                arrayOf(WORK, WORK, WORK, WORK, CARRY, MOVE, MOVE),
                "HarvesterBig",
                CreepOptions(Role.HARVESTER)
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
        if (creepMemory.role == "upgrader") {
            Upgrader.run(creep)
        }
    }

}

private fun populationControl(
    minPopulations: Array<Pair<Role, Int>>,
    creeps: Map<String, Creep>,
    spawn: StructureSpawn
) {
    for ((role, min) in minPopulations) {
        val current = creeps.filter { (_, creep) -> TutorialMemory(creep.memory).role == role.name.toLowerCase() }
        if (current.size < min) {
            val newName = "${role.name}_${Game.time}"
            val body = arrayOf(WORK, CARRY, MOVE)
            val code = spawn.spawnCreep(body, newName, CreepOptions(role))

            when (code) {
                OK -> console.log("spawning $newName with body $body")
                ERR_BUSY -> console.log("busy")
                ERR_NOT_ENOUGH_ENERGY -> run { } // do nothing
                else -> console.log("unhandled error code $code")
            }

        }
    }
}

public fun houseKeeping(creeps: Map<String, Creep>) {
    val creepsFromMemory = Memory.creeps
    if (creepsFromMemory == null) return

    for ((creepName, _) in jsonToMap<Creep>(creepsFromMemory)) {
        if (creeps[creepName] == null) {
            console.log("deleting obselete memory entry for creep $creepName")
            delete(Memory.creeps!![creepName])
        }
    }
}

