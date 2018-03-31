package screeps.game.one


import screeps.game.one.behaviours.BusyBehaviour
import screeps.game.one.behaviours.IdleBehaviour
import screeps.game.one.behaviours.RefillEnergy
import screeps.game.one.kreeps.BodyDefinition
import screeps.game.tutorials.tutorial4.houseKeeping
import types.*

object Context{
    //built-in
    var creeps : Map<String,Creep> = emptyMap()
    var rooms : Map<String,Room> = emptyMap()
    var structures: Map<String, Structure> = emptyMap()
    var constructionSites: Map<String, ConstructionSite> = emptyMap()

    //synthesized
    var targets: Map<String, Creep> = emptyMap()
}

fun gameLoop() {

    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)
    Context.rooms = Game.roomsMap()
    Context.creeps = jsonToMap(Game.creeps)
    Context.structures = jsonToMap(Game.structures)
    Context.constructionSites = jsonToMap(Game.constructionSites)
    Context.targets = Context.creeps.filter { it.value.memory.targetId != null }.mapKeys { (_, creep) -> creep.memory.targetId!! }

    houseKeeping(Context.creeps)

    val energySources = mainSpawn.room.findEnergy()
    val minWorkers = energySources.size * 4
    val minMiners = energySources.size

    if (Context.creeps.filter { it.key.startsWith(BodyDefinition.MINER.name) }.size < minMiners) {
        if (mainSpawn.room.energyAvailable >= BodyDefinition.MINER_BIG.getCost()) {
            mainSpawn.spawn(BodyDefinition.MINER_BIG)
        } else {
            mainSpawn.spawn(BodyDefinition.MINER)
        }
    }
    if (Context.creeps.filter { it.key.startsWith(BodyDefinition.BASIC_WORKER.name) }.size < minWorkers) {
        //spawn creeps
        mainSpawn.spawn(BodyDefinition.BASIC_WORKER)
    }

    for ((_, room) in Context.rooms) {
        if (room.memory.lastEnergy != room.energyAvailable) {
            room.memory.lastEnergy = room.energyAvailable
        }
    }

    val refillEnergy = RefillEnergy()
    val idleBehaviour = IdleBehaviour()
    for ((_, creep) in Context.creeps) {

        when (creep.memory.state) {
            CreepState.UNKNOWN -> TODO()
            CreepState.IDLE -> idleBehaviour.run(creep, mainSpawn)
            CreepState.REFILL -> refillEnergy.run(creep)
            else -> BusyBehaviour.run(creep) //TODO make dis better

        }
    }

    //println("cpu used this tick: ${Game.cpu.getUsed()}")

}
