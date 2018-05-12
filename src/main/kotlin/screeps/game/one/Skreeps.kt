package screeps.game.one


import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import screeps.game.one.behaviours.BusyBehaviour
import screeps.game.one.behaviours.IdleBehaviour
import screeps.game.one.behaviours.RefillEnergy
import screeps.game.one.building.buildStorage
import screeps.game.one.building.buildTowers
import screeps.game.one.kreeps.BodyDefinition
import screeps.game.tutorials.tutorial4.houseKeeping
import types.base.get
import types.base.global.*
import types.base.prototypes.ConstructionSite
import types.base.prototypes.Creep
import types.base.prototypes.Room
import types.base.prototypes.findEnergy
import types.base.prototypes.structures.Structure
import types.base.prototypes.structures.StructureSpawn
import types.base.prototypes.structures.StructureTower
import types.base.toMap
import types.extensions.copy
import types.extensions.lazyPerTick

object Context {
    //built-in
    val creeps: Map<String, Creep> by lazyPerTick { Game.creeps.toMap() }
    //val rooms: Map<String, Room> by lazyPerTick { Game.rooms.toMap() }
    val rooms: Map<String, Room> = Game.rooms.toMap()
    val myStuctures: Map<String, Structure> by lazyPerTick { Game.structures.toMap() }
    val constructionSites: Map<String, ConstructionSite> by lazyPerTick { Game.constructionSites.toMap() }

    //synthesized
    val targets: Map<String, Creep> by lazyPerTick { creepsByTarget() }
    //val towers: List<StructureTower> by lazyPerTick {
    //    myStuctures.values.filter { it.structureType == STRUCTURE_TOWER }.map { it as StructureTower }
    //}
    val towers: List<StructureTower> by lazyPerTick { myStuctures.values.filter { it.structureType == STRUCTURE_TOWER } as List<StructureTower> }

    private fun creepsByTarget(): Map<String, Creep> {
        return Context.creeps.filter { it.value.memory.targetId != null }
            .mapKeys { (_, creep) -> creep.memory.targetId!! }
    }
}


fun gameLoop() {

    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"])!!

    houseKeeping(Context.creeps)

    val energySources = mainSpawn.room.findEnergy()
    val minWorkers = energySources.size * 2
    val minMiners = energySources.size

    if (Context.creeps.count { it.key.startsWith(BodyDefinition.MINER.name) } < minMiners) {
        if (mainSpawn.room.energyAvailable >= BodyDefinition.MINER_BIG.cost) {
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

@Serializable
data class SuperAge(var age: Int = 21, var deceased: Boolean = false)

@Serializable
data class CreepRequest(val name: String = "joe", var age: SuperAge = SuperAge())

@Serializable
data class CreepRequestList(val creeps: List<CreepRequest> = emptyList())

var Memory.list: CreepRequestList?
    get() {
        val internal = this.asDynamic().testlist
        return if (internal == null) null else JSON.parse<CreepRequestList>(internal)
    }
    set(value) {
        val strinversion = if (value == null) null else JSON.stringify(value)
        println("stringified=$strinversion")
        this.asDynamic().testlist = strinversion
    }

fun sandbox() {
    val list = Memory.list
    if (list == null) {
        Memory.list = CreepRequestList(listOf(CreepRequest(), CreepRequest("phillip")))
        println("inserting $list of ${Memory.list!!.creeps.first()}")
    } else {
        println("list is $list}")
        for ((name, age) in list.creeps) {
            println("$name is $age old")
        }
        Memory.list = null

    }


}