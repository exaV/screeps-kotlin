package screeps.game.one.behaviours

import screeps.game.one.*
import screeps.game.one.building.buildRoads
import screeps.game.one.kreeps.BodyDefinition
import traveler.travelTo
import types.base.global.*
import types.base.prototypes.Creep
import types.base.prototypes.Room
import types.base.prototypes.findConstructionSites
import types.base.prototypes.findStructures
import types.base.prototypes.structures.EnergyContainingStructure
import types.base.prototypes.structures.Structure
import types.base.prototypes.structures.StructureSpawn

class IdleBehaviour {
    fun structuresThatNeedRepairing(): List<Structure> {
        if (Context.rooms.size > 1) println("not repairing myStuctures in all rooms")
        val room = Context.rooms.values.first()

        return room.findStructures().filterNot { Context.targets.containsKey(it.id) }
            .filter { it.hits < it.hitsMax / 2 }
            .sortedBy { it.hits }
            .take(5) //TODO only repairing 5 is arbitrary
    }

    val structureThatNeedRepairing = structuresThatNeedRepairing()
    var structureThatNeedRepairingIndex = 0

    fun run(creep: Creep, spawn: StructureSpawn) {
        if (creep.memory.missionId != null) return // we do not care about creeps on a mission
        creep.memory.targetId = null //just making sure it is reset


        val constructionSite = creep.findClosest(creep.room.findConstructionSites())
        val controller = creep.room.controller

        val towersInNeedOfRefill = Context.towers.filter { it.room == creep.room && it.energy < it.energyCapacity }
        when {
        //make sure spawn does not dry up
            notEnoughtSpawnEnergy(creep.room) -> {
                creep.memory.state = CreepState.TRANSFERRING_ENERGY
            }

        //make sure towe does not dry up
            towersInNeedOfRefill.isNotEmpty() -> {
                creep.memory.state = CreepState.TRANSFERRING_ENERGY
                creep.memory.targetId = towersInNeedOfRefill.first().id
            }

            creep.name.startsWith(BodyDefinition.HAULER.name) && creep.room.storage != null -> {
                creep.memory.state = CreepState.TRANSFERRING_ENERGY
                creep.memory.targetId = creep.room.storage!!.id
            }

        //check if we need to construct something
            constructionSite != null -> {
                creep.memory.state = CreepState.CONSTRUCTING
                creep.memory.targetId = constructionSite.id
            }
        //check if we need to upgrade the controller
            controller != null && controller.level < 8 && Context.creeps.none { it.value.memory.state == CreepState.UPGRADING } -> {
                creep.memory.state = CreepState.UPGRADING
                creep.memory.targetId = controller.id
            }
            structureThatNeedRepairing.isNotEmpty() && structureThatNeedRepairingIndex < structureThatNeedRepairing.size -> {
                val structure = structureThatNeedRepairing[structureThatNeedRepairingIndex++]
                creep.memory.state = CreepState.REPAIR
                creep.memory.targetId = structure.id
                println("repairing ${structure.structureType} (${structure.id})")
            }

            controller?.level == 8 && controller.ticksToDowngrade < 10_000 && Context.creeps.none { it.value.memory.state == CreepState.UPGRADING } -> {
                creep.memory.state = CreepState.UPGRADING
                creep.memory.targetId = controller.id
            }

            creep.room.energyAvailable < creep.room.energyCapacityAvailable -> {
                creep.memory.state = CreepState.TRANSFERRING_ENERGY

            }
        //if still idle upgrade controller
            controller != null && controller.level < 8 -> {
                creep.memory.state = CreepState.UPGRADING
                creep.memory.targetId = controller.id
            }
            else -> { //get out of the way
                //val xScale = random()
                //val yScale = random()
                //creep.moveTo(RoomPosition(spawn.pos.x + xScale * 10, spawn.pos.y + yScale * 10, ""))
            }

        }

    }

    private fun notEnoughtSpawnEnergy(room: Room) =
        room.energyAvailable < BodyDefinition.BASIC_WORKER.cost
                // or at least 2/3 of energy available
                || room.energyCapacityAvailable > BodyDefinition.BASIC_WORKER.cost
                && room.energyAvailable < room.energyCapacityAvailable * 2.0 / 3.0
}


object BusyBehaviour {
    fun run(creep: Creep) {

        if (creep.carry.energy == 0) {
            creep.memory.state = CreepState.REFILL
            creep.memory.targetId = null
            return
        }


        if (creep.memory.state == CreepState.TRANSFERRING_ENERGY) {
            fun findTarget(): Structure? {
                val targets = creep.room.findStructures()
                    .filter { (it.structureType == STRUCTURE_EXTENSION || it.structureType == STRUCTURE_SPAWN) }
                    .map { (it as EnergyContainingStructure) }
                    .filter { it.energy < it.energyCapacity }

                if (targets.isNotEmpty()) {
                    return creep.findClosest(targets as List<Structure>)!!
                } else return null;
            }

            val target = if (creep.memory.targetId != null) Game.getObjectById(creep.memory.targetId) else findTarget()

            if (target != null) {
                val code = creep.transfer(target, RESOURCE_ENERGY)
                when (code) {
                    OK -> kotlin.run { }
                    ERR_NOT_IN_RANGE -> creep.travelTo(target.pos)
                    else -> creep.memory.state = CreepState.IDLE
                }
            } else {
                creep.memory.state = CreepState.IDLE
                creep.memory.targetId = null
            }
        }


        if (creep.memory.state == CreepState.UPGRADING) {
            val controller = creep.room.controller!!
            if (creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                creep.travelTo(controller.pos)
            }
        }

        if (creep.memory.state == CreepState.CONSTRUCTING) {
            val constructionSite = Context.constructionSites[creep.memory.targetId!!]
            if (constructionSite != null) {
                if (creep.build(constructionSite) == ERR_NOT_IN_RANGE) {
                    creep.travelTo(constructionSite.pos);
                }
            } else {
                println("construction of ${creep.memory.targetId} is done")
                creep.memory.targetId = null
                creep.memory.state = CreepState.IDLE
                buildRoads(creep.room)
            }
        }

        if (creep.memory.state == CreepState.REPAIR) {
            require(creep.memory.targetId != null)
            val structure = Game.getObjectById<Structure>(creep.memory.targetId!!)

            fun done() {
                println("finished repairing ${creep.memory.targetId}")
                creep.memory.state = CreepState.IDLE
                creep.memory.targetId = null
            }
            if (structure == null || structure.hits == structure.hitsMax) {
                done()
            } else {
                if (creep.repair(structure) == ERR_NOT_IN_RANGE) {
                    creep.travelTo(structure.pos)
                }
            }
        }

    }
}