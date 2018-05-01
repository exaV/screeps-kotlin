package screeps.game.one


import screeps.game.one.behaviours.BusyBehaviour
import screeps.game.one.behaviours.IdleBehaviour
import screeps.game.one.behaviours.RefillEnergy
import screeps.game.one.building.buildStorage
import screeps.game.one.building.buildTowers
import screeps.game.one.kreeps.BodyDefinition
import screeps.game.tutorials.tutorial4.houseKeeping
import types.*
import types.base.global.Game
import types.extensions.lazyPerTick


object Context {
    //built-in
    val creeps: Map<String, Creep> by lazyPerTick { jsonToMap<Creep>(Game.creeps) }
    val rooms: Map<String, Room> by lazyPerTick { jsonToMap<Room>(Game.rooms) }
    val myStuctures: Map<String, Structure> by lazyPerTick { jsonToMap<Structure>(Game.structures) }
    val constructionSites: Map<String, ConstructionSite> by lazyPerTick { jsonToMap<ConstructionSite>(Game.constructionSites) }

    //synthesized
    val targets: Map<String, Creep> by lazyPerTick { creepsByTarget() }
    val towers: List<StructureTower> by lazyPerTick {
        myStuctures.values.filter { it.structureType == STRUCTURE_TOWER }.map { it as StructureTower }
    }

    private fun creepsByTarget(): Map<String, Creep> {
        return Context.creeps.filter { it.value.memory.targetId != null }
            .mapKeys { (_, creep) -> creep.memory.targetId!! }
    }
}


fun gameLoop() {

    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"]!! as StructureSpawn)

    houseKeeping(Context.creeps)

    val energySources = mainSpawn.room.findEnergy()
    val minWorkers = energySources.size * 4
    val minMiners = energySources.size

    if (Context.creeps.count { it.key.startsWith(BodyDefinition.MINER.name) } < minMiners) {
        if (mainSpawn.room.energyAvailable >= BodyDefinition.MINER_BIG.getCost()) {
            mainSpawn.spawn(BodyDefinition.MINER_BIG)
        } else {
            mainSpawn.spawn(BodyDefinition.MINER)
        }
    } else if (Context.creeps.count { it.key.startsWith(BodyDefinition.BASIC_WORKER.name) } < minWorkers) {
        //spawn creeps
        mainSpawn.spawn(BodyDefinition.BASIC_WORKER)
    } else if (Context.creeps.count { it.key.startsWith(BodyDefinition.HAULER.name) } < minMiners
        && Context.myStuctures.any { it.value.structureType == STRUCTURE_STORAGE }) {
        mainSpawn.spawn(BodyDefinition.HAULER)
    }

    for ((_, room) in Context.rooms) {
        buildStorage(room)
        buildTowers(room)

        val hostiles = room.find<Creep>(FIND_HOSTILE_CREEPS)

        for (tower in Context.towers) {
            if (tower.room.name != room.name) continue
            if (hostiles.isNotEmpty() && tower.energy > 0) {
                tower.attack(hostiles.minBy { it.hits }!!)
            }
        }

        if (room.memory.lastEnergy != room.energyAvailable) {
            room.memory.lastEnergy = room.energyAvailable
        }
    }

    val refillEnergy = RefillEnergy()
    val idleBehaviour = IdleBehaviour()
    for ((_, creep) in Context.creeps) {
        if (creep.spawning) continue

        when (creep.memory.state) {
            CreepState.UNKNOWN -> {
                println("creep ${creep.name} was in UKNOWN state. Resuming from IDLE")
                idleBehaviour.run(creep, mainSpawn)
            }
            CreepState.IDLE -> idleBehaviour.run(creep, mainSpawn)
            CreepState.REFILL -> refillEnergy.run(creep)
            else -> BusyBehaviour.run(creep) //TODO make dis better
        }
    }

    //println("cpu used this tick: ${Game.cpu.getUsed()}")
    var pos = mainSpawn.pos.copy(mainSpawn.pos.x - 2)

    sandbox()

}

fun sandbox() {


}