package screeps.game.one.behaviours

import screeps.game.one.BetterCreepMemory
import screeps.game.one.Context
import screeps.game.one.CreepState
import screeps.game.one.findClosest
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
            val code = room.createConstructionSite(tile.x, tile.y, STRUCTURE_ROAD)
            when (code) {
                OK -> run { }
                else -> println("could not place road at $tile code=($code)")
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

object IdleBehaviour {
    fun run(creep: Creep, creepMemory: BetterCreepMemory, spawn: StructureSpawn) {

        //make sure spawn does not dry up
        if (creep.room.energyAvailable < BodyDefinition.BASIC_WORKER.getCost()) {
            creepMemory.state = CreepState.TRANSFERRING_ENERGY

            return
        }

        //check if we need to construct something
        val constructionSite = creep.findClosest(creep.room.findConstructionSites())
        if (constructionSite != null) {
            creepMemory.state = CreepState.CONSTRUCTING
            creepMemory.building = constructionSite.id

            return
        }

        //check if we need to upgrade the controller
        val controller = creep.room.controller
        if (controller != null && controller.level < 8 && Context.creeps.filter { BetterCreepMemory(it.value.memory).upgrading != null }.size < 3) {
            creepMemory.state = CreepState.UPGRADING
            creepMemory.upgrading = controller.id

            return
        }

        if (creep.room.energyAvailable < creep.room.energyCapacityAvailable) {
            creepMemory.state = CreepState.TRANSFERRING_ENERGY

            return
        }

        //if still idle upgrade controller
        if (controller != null && controller.level < 8) {
            creepMemory.state = CreepState.UPGRADING
            creepMemory.upgrading = controller.id

            return
        }

        //get out of the way
        val xScale = random()
        val yScale = random()
        creep.moveTo(RoomPosition(spawn.pos.x + xScale * 10, spawn.pos.y + yScale * 10, ""))

    }
}

object BusyBehaviour {
    fun run(creep: Creep, creepMemory: BetterCreepMemory, spawn: StructureSpawn) {

        if (creep.carry.energy == 0) {
            creepMemory.state = CreepState.REFILL
            creepMemory.upgrading = null
            creepMemory.building = null
            return
        }


        if (creepMemory.state == CreepState.TRANSFERRING_ENERGY) {
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
                    else -> creepMemory.state = CreepState.IDLE
                }

            } else {
                creepMemory.state = CreepState.IDLE
            }
        }

        if (creepMemory.state == CreepState.UPGRADING) {
            val controller = creep.room.controller!!
            if (creep.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                creep.moveTo(controller.pos);
            }
        }

        if (creepMemory.state == CreepState.CONSTRUCTING) {
            val constructionSite = Game.constructionsSitesMap()[creepMemory.building!!]
            if (constructionSite != null) {
                if (creep.build(constructionSite) == ERR_NOT_IN_RANGE) {
                    creep.moveTo(constructionSite.pos, VisualizePath(stroke = "#ffffff"));
                }
            } else {
                println("construction of ${creepMemory.building} is done")
                creepMemory.building = null
                creepMemory.state = CreepState.IDLE
                buildRoads(creep.room)
            }
        }

    }
}