package screeps.game.one.behaviours

import screeps.game.one.*
import screeps.game.one.kreeps.BodyDefinition
import types.*
import kotlin.js.Math.random

fun buildRoads(room: Room) {
    val controller = room.controller
    if (controller == null) {
        println("cannot buildRoads() in room which is not under our control")
        return
    }
    println("building roads in room $room")

    val spawns = room.find<StructureSpawn>(FIND_MY_SPAWNS)
    val energySources = room.findEnergy()

    fun buildPathBetween(a: RoomPosition, b: RoomPosition) {
        val path = room.findPath(a, b, FindPathOpts(ignoreCreeps = true))
        for (tile in path) {
            val stuff = room.lookAt(tile.x, tile.y)
            val roadExistsAtTile = stuff.any {
                (it.type == LOOK_STRUCTURES && it.structure!!.structureType == STRUCTURE_ROAD)
                        || (it.type == LOOK_CONSTRUCTION_SITES && it.constructionSite!!.structureType == STRUCTURE_ROAD)
            }
            if (roadExistsAtTile) continue

            val code = room.createConstructionSite(tile.x, tile.y, STRUCTURE_ROAD)
            when (code) {
                OK -> run { }
                else -> println("could not place road at [x=${tile.x},y=${tile.y}] code=($code)")
            }
        }
    }
    //build roads from controller to each spawn
    for (spawn in spawns) {
        buildPathBetween(controller.pos, spawn.pos)

        //build roads from each spawn to each source
        for (source in energySources) {
            buildPathBetween(source.pos, spawn.pos)
        }
    }

}

class IdleBehaviour {
    fun structuresThatNeedRepairing(): List<Structure> {
        val structureThatNeedRepairing = ArrayList<Structure>(Context.structures.size)

        for ((id, structure) in Context.structures) {
            if (Context.targets.containsKey(id)) continue //TODO this may not work forever


            if (structure.hits < structure.hitsMax / 2) {
                structureThatNeedRepairing.add(structure)
            }
        }

        structureThatNeedRepairing.sortBy { it.hits }
        return structureThatNeedRepairing.take(5)
    }

    val structureThatNeedRepairing = structuresThatNeedRepairing()
    var structureThatNeedRepairingIndex = 0

    fun run(creep: Creep, spawn: StructureSpawn) {
        creep.memory.targetId = null //just making sure it is reset

        val constructionSite = creep.findClosest(creep.room.findConstructionSites())
        val controller = creep.room.controller

        when {
        //make sure spawn does not dry up
            creep.room.energyAvailable < BodyDefinition.BASIC_WORKER.getCost() -> {
                creep.memory.state = CreepState.TRANSFERRING_ENERGY
            }

        //check if we need to construct something
            constructionSite != null -> {
                creep.memory.state = CreepState.CONSTRUCTING
                creep.memory.targetId = constructionSite.id
            }
        //check if we need to upgrade the controller
            controller != null && controller.level < 8 && Context.creeps.filter { it.value.memory.state == CreepState.UPGRADING }.size < 3 -> {
                creep.memory.state = CreepState.UPGRADING
                creep.memory.targetId = controller.id
            }
            structureThatNeedRepairing.isNotEmpty() && structureThatNeedRepairingIndex < structureThatNeedRepairing.size -> {
                val structure = structureThatNeedRepairing[structureThatNeedRepairingIndex++]
                creep.memory.state = CreepState.REPAIR
                creep.memory.targetId = structure.id
                println("repairing ${structure.structureType} (${structure.id})")
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
                val xScale = random()
                val yScale = random()
                creep.moveTo(RoomPosition(spawn.pos.x + xScale * 10, spawn.pos.y + yScale * 10, ""))
            }

        }

    }
}

object BusyBehaviour {
    fun run(creep: Creep) {

        if (creep.carry.energy == 0) {
            creep.memory.state = CreepState.REFILL
            creep.memory.targetId = null
            return
        }


        if (creep.memory.state == CreepState.TRANSFERRING_ENERGY) {
            val targets = creep.room.findStructures()
                .filter { (it.structureType == STRUCTURE_EXTENSION || it.structureType == STRUCTURE_SPAWN) }
                .map { (it as StructureSpawn) }
                .filter { it.energy < it.energyCapacity }


            if (targets.isNotEmpty()) {
                val closest: StructureSpawn = creep.findClosest(targets)!!

                val code = creep.transfer(closest, RESOURCE_ENERGY)
                when (code) {
                    OK -> kotlin.run { }
                    ERR_NOT_IN_RANGE -> creep.moveTo(closest.pos, VisualizePath(stroke = "#ffffff"))
                    else -> creep.memory.state = CreepState.IDLE
                }

            } else {
                creep.memory.state = CreepState.IDLE
            }
        }

        if (creep.memory.state == CreepState.UPGRADING) {
            val controller = creep.room.controller!!
            if (creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                creep.moveTo(controller.pos);
            }
        }

        if (creep.memory.state == CreepState.CONSTRUCTING) {
            val constructionSite = Game.constructionsSitesMap()[creep.memory.targetId!!]
            if (constructionSite != null) {
                if (creep.build(constructionSite) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(constructionSite.pos, VisualizePath(stroke = "#ffffff"));
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
            val structure = Game.getObjectById<OwnedStructure>(creep.memory.targetId!!)

            fun done() {
                println("finished repairing ${creep.memory.targetId}")
                creep.memory.state = CreepState.IDLE
                creep.memory.targetId = null
            }
            if (structure == null || structure.hits == structure.hitsMax) {
                done()
            } else {
                require(structure.my)
                if (creep.repair(structure) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(structure.pos)
                }
            }
        }

    }
}