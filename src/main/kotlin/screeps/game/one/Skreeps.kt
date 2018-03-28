package screeps.game.one


import screeps.game.one.behaviours.BusyBehaviour
import screeps.game.one.behaviours.IdleBehaviour
import screeps.game.one.behaviours.RefillEnergy
import screeps.game.one.kreeps.BodyDefinition
import screeps.game.tutorials.tutorial4.houseKeeping
import types.*

object Context{
    var creeps : Map<String,Creep> = emptyMap()
    var rooms : Map<String,Room> = emptyMap()
}

fun gameLoop() {

    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    Context.rooms = Game.roomsMap()
    Context.creeps = jsonToMap(Game.creeps)

    houseKeeping(Context.creeps)

    val minWorkers = mainSpawn.room.findEnergy().size * 4
    val minMiners = mainSpawn.room.findEnergy().size


    if (Context.creeps.filter { it.key.startsWith(BodyDefinition.MINER.name) }.size < minMiners) {
        mainSpawn.spawn(BodyDefinition.MINER)
    }
    if (Context.creeps.filter { it.key.startsWith(BodyDefinition.BASIC_WORKER.name) }.size < minWorkers) {
        //spawn creeps
        mainSpawn.spawn(BodyDefinition.BASIC_WORKER)
    }

    for ((roomName, room) in Context.rooms) {
        if (room.memory.lastEnergy != room.energyAvailable) {
            room.memory.lastEnergy = room.energyAvailable
        }
    }

    for ((_, creep) in Context.creeps) {
        val creepMemory = BetterCreepMemory(creep.memory)

        when (creepMemory.state) {
            CreepState.UNKNOWN -> TODO()
            CreepState.IDLE -> IdleBehaviour.run(creep, creepMemory, mainSpawn)
            CreepState.REFILL -> RefillEnergy.run(creep, creepMemory)
            else -> BusyBehaviour.run(creep, creepMemory, mainSpawn) //TODO make dis better

        }
    }

    //println("cpu used this tick: ${Game.cpu.getUsed()}")

}
