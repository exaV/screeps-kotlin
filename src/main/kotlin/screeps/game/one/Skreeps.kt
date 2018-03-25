package screeps.game.one


import screeps.game.one.behaviours.BusyBehaviour
import screeps.game.one.behaviours.IdleBehaviour
import screeps.game.one.behaviours.RefillEnergy
import screeps.game.one.kreeps.BodyDefinition
import screeps.game.tutorials.tutorial4.houseKeeping
import types.*

fun gameLoop() {

    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    val creeps = Game.creepsMap()
    val rooms = Game.roomsMap()

    houseKeeping(creeps)

    val minWorkers = mainSpawn.room.findEnergy().size * 3
    if (creeps.size < minWorkers) {
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
            CreepState.REFILL -> RefillEnergy.run(creep, creepMemory)
            else -> BusyBehaviour.run(creep, creepMemory, mainSpawn) //TODO make dis better

        }
    }

    // println("Used $cpuUsage this tick")
    println("cpu used this tick: ${Game.cpu.getUsed()}")

}
