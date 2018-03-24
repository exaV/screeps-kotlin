package screeps.game.one


import screeps.game.one.behaviours.BusyBehaviour
import screeps.game.one.behaviours.IdleBehaviour
import screeps.game.one.behaviours.RefillBehaviour
import screeps.game.tutorials.tutorial4.houseKeeping
import types.*


enum class BodyDefinition(val bodyType: Array<BodyType>) {
    BASIC_WORKER(arrayOf(WORK, CARRY, MOVE)),
    BIG_WORKER(arrayOf(WORK, WORK, WORK, WORK, CARRY, MOVE, MOVE))
}

class CreepOptions(state: CreepState) {
    val memory: CreepMemory = object : CreepMemory {
        val state: String = state.name
    }
}

fun StructureSpawn.spawn(bodyDefinition: BodyDefinition) {
    val newName = "${bodyDefinition.name}_${Game.time}"
    val code = this.spawnCreep(bodyDefinition.bodyType, newName, CreepOptions(CreepState.REFILL))
    when (code) {
        OK -> console.log("spawning $newName with body ${bodyDefinition.bodyType}")
        ERR_BUSY -> console.log("busy")
        ERR_NOT_ENOUGH_ENERGY -> run { } // do nothing
        else -> console.log("unhandled error code $code")
    }
}

fun <T : RoomObject> Creep.findClosest(roomObjects: Array<out T>): T? {
    var closest: T? = null
    var minDistance = Double.MAX_VALUE
    for (roomObject in roomObjects) {
        val dist = (roomObject.pos.x - this.pos.x) * (roomObject.pos.x - this.pos.x) +
                (roomObject.pos.y - this.pos.y) * (roomObject.pos.y - this.pos.y)

        if (dist < minDistance) {
            minDistance = dist
            closest = roomObject
        }
    }
    return closest
}

fun Creep.findClosestEnergySource(): Source? {
    val sources = this.room.findEnergy()
    return findClosest(sources)
}

fun measureCpu(block: () -> Unit): Int {
    val startCpu = Game.cpu.limit
    block()
    val usedCpu = startCpu - Game.cpu.limit
    return usedCpu
}

fun gameLoop() {
    val cpuUsage = measureCpu {

        val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
        val creeps = Game.creepsMap()
        val rooms = Game.roomsMap()

        houseKeeping(creeps)

        if (creeps.isEmpty() || creeps.none { (_, creep) ->
                BetterCreepMemory(creep.memory).state == CreepState.IDLE
                        || BetterCreepMemory(creep.memory).state == CreepState.REFILL
            }) {
            //spawn creeps
            println("spawing...")
            mainSpawn.spawn(BodyDefinition.BASIC_WORKER)
        }

        for ((roomName, room) in rooms) {
            if (room.memory.lastEnergy != room.energyAvailable) {
                room.memory.lastEnergy = room.energyAvailable
            }
        }

        for ((_, creep) in creeps) {
            val creepMemory = BetterCreepMemory(creep.memory)

            when (creepMemory.state) {
                CreepState.UNKNOWN -> TODO()
                CreepState.IDLE -> IdleBehaviour.run(creep, creepMemory, mainSpawn)
                CreepState.REFILL -> RefillBehaviour.run(creep, creepMemory)
                else -> BusyBehaviour.run(creep, creepMemory, mainSpawn) //TODO make dis better

            }
        }
    }
    println("Used $cpuUsage this tick")


}
