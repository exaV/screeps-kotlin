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
    val towers: List<StructureTower> by lazyPerTick {
        Context.myStuctures.values.filter { it.structureType == STRUCTURE_TOWER }.map { it as StructureTower }
    }

    private fun creepsByTarget(): Map<String, Creep> {
        return Context.creeps.filter { it.value.memory.targetId != null }
            .mapKeys { (_, creep) -> creep.memory.targetId!! }
    }
}

val StructureController.availableExtensions
    get() = when (level) {
        1 -> 0
        2 -> 5
        3 -> 10
        4 -> 20
        5 -> 30
        6 -> 40
        7 -> 50
        8 -> 60
        else -> 0 //will not happen
    }

fun buildExtensions(room: Room) {
    require(room.controller?.my == true)

    val spawn = room.find<StructureSpawn>(FIND_MY_SPAWNS).first()

    val startPos = spawn.pos
    val numberOfExtensions: Int =
        room.find<Structure>(FIND_STRUCTURES).count { it.structureType == STRUCTURE_EXTENSION }
    val toPlace = room.controller!!.availableExtensions - numberOfExtensions
    var placed = 0

    val energySources = room.findEnergy()

    require(toPlace >= 0)
    val constructionSites = ArrayList<ConstructionSite>()
    while (placed < toPlace) {
        //find a road from spawn to energy source
        for (source in energySources) {

        }
    }
}

val StructureController.availableStorage
    get() = when {
        level >= 4 -> 1
        else -> 0
    }


fun buildStorage(room: Room) {
    if (room.controller?.my != true) return //not our room
    if (room.controller?.availableStorage != 1) return //cannot build storage yet

    val hasStorage = room.storage != null
            || Context.constructionSites.values.any { it.structureType == STRUCTURE_STORAGE && it.room.name == room.name }
    if (hasStorage) return //already built or being  built

    val spawn = room.find<StructureSpawn>(FIND_MY_SPAWNS).first()

    var placed = false
    var pos = spawn.pos.copy(spawn.pos.x - 2)
    while (!placed) {
        val code = room.createConstructionSite(pos, STRUCTURE_STORAGE)
        when (code) {
            OK -> placed = true
            ERR_INVALID_TARGET -> pos = pos.copy(x = pos.x - 1)
            else -> println("unexpected return value $code when attempting to place storage")
        }
    }
}

val StructureController.availableTowers
    get() = when (level) {
        3, 4 -> 1
        5, 6 -> 2
        7 -> 3
        8 -> 6
        else -> 0
    }

fun buildTowers(room: Room) {
    if (room.controller?.my != true) return //not under control

    val numberOfTowers =
        Context.constructionSites.values.count { it.room.name == room.name && it.structureType == STRUCTURE_TOWER } + Context.myStuctures.values.count { it.room.name == room.name && it.structureType == STRUCTURE_TOWER }
    val towersToPlace = room.controller!!.availableTowers - numberOfTowers
    if (towersToPlace == 0) return //no need to place towers


    require(room.controller?.my == true)
    val spawn = room.find<StructureSpawn>(FIND_MY_SPAWNS).first()

    require(towersToPlace >= 0)
    var placed = 0

    var x = spawn.pos.x
    var y = spawn.pos.y + 1

    while (placed < towersToPlace) {
        y += 1
        val success = room.createConstructionSite(x, y, STRUCTURE_TOWER)
        when (success) {
            OK -> placed += 1
            ERR_INVALID_TARGET -> run { }
            else -> println("unexpected return value $success when attempting to place tower")
        }
    }

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

        when (creep.memory.state) {
            CreepState.UNKNOWN -> TODO()
            CreepState.IDLE -> idleBehaviour.run(creep, mainSpawn)
            CreepState.REFILL -> refillEnergy.run(creep)
            else -> BusyBehaviour.run(creep) //TODO make dis better

        }

    }

    //println("cpu used this tick: ${Game.cpu.getUsed()}")
    var pos = mainSpawn.pos.copy(mainSpawn.pos.x - 2)

}
