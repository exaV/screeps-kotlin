package screeps.game.one


import screeps.api.*
import screeps.api.structures.Structure
import screeps.api.structures.StructureController
import screeps.api.structures.StructureSpawn
import screeps.api.structures.StructureTower
import screeps.game.one.behaviours.BusyBehaviour
import screeps.game.one.behaviours.IdleBehaviour
import screeps.game.one.behaviours.RefillEnergy
import screeps.game.one.building.buildStorage
import screeps.game.one.building.buildTowers
import screeps.game.one.kreeps.BodyDefinition
import screeps.utils.lazyPerTick
import screeps.utils.toMap
import kotlin.collections.component1
import kotlin.collections.component2

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

fun <T> Iterable<Structure>.filterIsStrucure(structureType: StructureConstant): List<Structure> {
    return this.filter { it.structureType == structureType }

}


fun gameLoop() {
    Stats.tickStarts()

    if (Game.time % 100 == 0) { // TODO modulo seems expensive. Use something else
        houseKeeping()
    }

    val mainSpawn: StructureSpawn = (Game.spawns["Spawn1"])!!
    val secondarySpawn: StructureSpawn? = (Game.spawns["Spawn5"]) //FIXME 
    secondarySpawn?.also {
        val creeps = secondarySpawn.room.find<Creep>(FIND_MY_CREEPS)
        if (creeps.count { it.name.startsWith(BodyDefinition.BASIC_WORKER.name) } < 3) {
            secondarySpawn.spawn(BodyDefinition.BASIC_WORKER)
        }

    }

    val energySources = mainSpawn.room.findEnergy()
    val minWorkers = energySources.size * 2
    val minMiners = energySources.size

    val minerCount = Context.creeps.count { it.key.startsWith(BodyDefinition.MINER.name) }
    val workerCount = Context.creeps.count { it.key.startsWith(BodyDefinition.BASIC_WORKER.name) }
    val haulerCount = Context.creeps.count { it.key.startsWith(BodyDefinition.HAULER.name) }
    if (minerCount < minMiners) {
        if (GlobalSpawnQueue.spawnQueue.count { it.bodyDefinition.name.startsWith(BodyDefinition.MINER.name) } < minMiners - minerCount) {
            // TODO we cannot spawn small miners
            GlobalSpawnQueue.push(BodyDefinition.MINER_BIG, KreepSpawnOptions(CreepState.REFILL))
        }
    }
    if (workerCount < minWorkers) {
        if (GlobalSpawnQueue.spawnQueue.count { it.bodyDefinition == BodyDefinition.BASIC_WORKER } < minWorkers - workerCount) {
            GlobalSpawnQueue.push(BodyDefinition.BASIC_WORKER)
        }
    }
    if (haulerCount < minMiners && mainSpawn.room.storage != null) {
        if (GlobalSpawnQueue.spawnQueue.count { it.bodyDefinition == BodyDefinition.HAULER } < minMiners - haulerCount) {
            GlobalSpawnQueue.push(BodyDefinition.HAULER)
        }
    }


//    if (Missions.activeMissions.isEmpty() && Missions.missionMemory.upgradeMissions.isEmpty()) {
//        val q = RoomUpgradeMission(mainSpawn.room.controller!!.missionId)
//        Missions.missionMemory.upgradeMissions.add(q.memory)
//        Missions.activeMissions.add(q)
//    }


    GlobalSpawnQueue.spawnCreeps(listOf(mainSpawn))

    for ((_, room) in Context.rooms) {
        Stats.write(room)


        buildStorage(room)
        buildTowers(room)

        val hostiles = room.find<Creep>(FIND_HOSTILE_CREEPS)
        for (tower in Context.towers) {
            if (tower.room.name != room.name) continue
            if (hostiles.isNotEmpty() && tower.energy > 0) {
                tower.attack(hostiles.minBy { it.hits }!!)
            }
        }
    }

    val refillEnergy = RefillEnergy()
    val idleBehaviour = IdleBehaviour()

    // remove completed mission TODO do this with mission.complete = true
    val removed = Missions.missionMemory.upgradeMissionMemory.removeAll {
        val controller = Game.getObjectById<StructureController>(it.controllerId)
        controller == null || !controller.my
    }
    if (removed) {
        println("removed a mission")
        Missions.activeMissions.clear()
        Missions.save()
    }

    Missions.load()

    for ((name, flag) in Game.flags) {
        if (name == "colonize" && Missions.activeMissions.none { it is ColonizeMission && it.pos.roomName == flag.pos.roomName }) {
            ColonizeMission.forRoom(flag.pos)
        }
    }
    for ((name, spawn) in Game.spawns) {
        if (spawn.my && spawn.room.controller!!.my && Missions.missionMemory.upgradeMissionMemory.none { it.controllerId == spawn.room.controller!!.id }) {
            RoomUpgradeMission.forRoom(spawn.room)
        }
    }

    Missions.update()

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

    GlobalSpawnQueue.save()
    Missions.save()

    Stats.tickEnds()
    sandbox()
}

fun sandbox() {


}

public fun houseKeeping() {
    js(
        """
        for (var name in Memory.creeps) {
            if (!Game.creeps[name]) {
                delete Memory.creeps[name];
                console.log('Clearing non-existing creep memory:', name);
            }
        }
        """
    )
}
