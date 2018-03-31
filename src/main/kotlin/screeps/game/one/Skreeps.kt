package screeps.game.one


import screeps.game.one.behaviours.BusyBehaviour
import screeps.game.one.behaviours.IdleBehaviour
import screeps.game.one.behaviours.RefillEnergy
import screeps.game.one.kreeps.BodyDefinition
import screeps.game.tutorials.tutorial4.houseKeeping
import types.*


object Context {
    //built-in
    val creeps: Map<String, Creep> by lazyPerTick { jsonToMap<Creep>(Game.creeps) }
    val rooms: Map<String, Room> by lazyPerTick { jsonToMap<Room>(Game.rooms) }
    val myStuctures: Map<String, Structure> by lazyPerTick { jsonToMap<Structure>(Game.structures) }
    val constructionSites: Map<String, ConstructionSite> by lazyPerTick { jsonToMap<ConstructionSite>(Game.constructionSites) }

    //synthesized
    val targets: Map<String, Creep> by lazyPerTick { creepsByTarget() }

    private fun creepsByTarget(): Map<String, Creep> {
        return Context.creeps.filter { it.value.memory.targetId != null }
            .mapKeys { (_, creep) -> creep.memory.targetId!! }
    }
}

fun getAvailableExtension(controllerLevel: Int) = when (controllerLevel) {
    1 -> 0
    2 -> 5
    3 -> 10
    4 -> 20
    5 -> 30
    6 -> 40
    7 -> 50
    8 -> 60
    else -> throw IllegalArgumentException("unexpected conrollerLevel $controllerLevel")
}

fun buildExtensions(room: Room) {
    require(room.controller?.my == true)


}

fun gameLoop() {

    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)

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
    } else if (Context.creeps.filter { it.key.startsWith(BodyDefinition.BASIC_WORKER.name) }.size < minWorkers) {
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
