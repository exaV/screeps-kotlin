package screeps.game.one.behaviours

import screeps.game.one.Context
import screeps.game.one.CreepState
import screeps.game.one.assignedEnergySource
import screeps.game.one.kreeps.BodyDefinition
import screeps.game.one.state
import types.*

class RefillEnergy {
    companion object {
        const val MAX_MINER_PER_SOURCE = 1
        const val MAX_CREEP_PER_MINE = 5
    }

    val droppedEnergyByRoom : MutableMap<Room,Array<Resource>> = mutableMapOf()

    val usedSourcesWithCreepCounts = Context.creeps
            .map { it.value.memory.assignedEnergySource }
            .filterNotNull()
            .groupingBy { it }
            .eachCount()

    fun run(creep: Creep) {
        if (creep.name.startsWith(BodyDefinition.MINER.name)) {
            miner(creep)
        } else {
            worker(creep)
        }
    }

    private fun worker(creep: Creep) {
        if (shouldContinueMininig(creep)) {
            val assignedEnergy: Resource = if (creep.memory.assignedEnergySource != null) {
                Game.getObjectById<Resource>(creep.memory.assignedEnergySource) ?: creep.requestEnergy() ?: return
            } else {
                val energy = creep.requestEnergy()
                if (energy == null) return //TODO what to do

                creep.memory.assignedEnergySource = energy.id
                energy
            }

            if (creep.pickup(assignedEnergy) == ERR_NOT_IN_RANGE) {
                creep.moveTo(assignedEnergy.pos)
            }
        } else {
            creep.memory.state = CreepState.IDLE
        }
    }

    fun Creep.requestEnergy(): Resource? {

        val droppedEnergy = droppedEnergyByRoom.getOrPut(this.room, {
            val e = this.room.findDroppedEnergy()
            e.sort({ a, b -> b.amount - a.amount })
            e
        })

        //find a source that is close and has some free spots
        for (energy in droppedEnergy) {
            if (usedSourcesWithCreepCounts.getOrElse(energy.id, { 0 }) < MAX_CREEP_PER_MINE) {
                //assign creep to energy source
                return energy
            }
        }

        return null
    }

    private fun miner(creep: Creep) {

        if (shouldContinueMininig(creep)) {
            var assignedSource = creep.memory.assignedEnergySource
            if (assignedSource == null) {
                val energySources = creep.room.findEnergy()
                val source = creep.requestSource(energySources)
                if (source == null){
                    println("no energy sources available for creep ${creep.name} in ${creep.room}")
                    return
                }
                creep.memory.assignedEnergySource = source.id
                assignedSource = source.id

            }

            val source = Game.getObjectById<Source>(assignedSource)!!

            val useContainerMining = creep.room.controller?.level ?: 0 >= 3 && creep.name.startsWith(BodyDefinition.MINER_BIG.name)
            if (useContainerMining) {
                containerMining(creep, source)
            } else {
                val code = creep.harvest(source)
                when (code) {
                    ERR_NOT_IN_RANGE -> {
                        val moveCode = creep.moveTo(source.pos, VisualizePath())
                        when (moveCode) {
                            OK -> {
                            }
                        //TODO handle no path
                            else -> println("unexpected code $moveCode when moving $creep to ${source.pos}")
                        }
                    }
                }
            }


        } else {
            creep.memory.state = CreepState.IDLE
        }
    }

    fun containerMining(creep: Creep, source: Source) {
        val sourceToContainerMaxRange = 3

        //TODO
        /*Container mining:
     If RCL > 3 we can place containers to reduce loss to decay of dropped resources.
     The miner needs to stand exactly on the container and repair it from time to time
     Obviously this is only beneficial if we already have a big miner
  */

        //TODO make sure the computations happen not all the time
        //TODO this assumes the container can be built by workers -> workers must be present
        data class Pos(val x: Int, val y: Int)

        val pathToSource = source.room.findPath(creep.pos, source.pos)

        //figure out where to place the container
        val containertile: Pos = if (pathToSource.size < 2) {
            Pos(creep.pos.x, creep.pos.y)
        } else {
            creep.moveByPath(pathToSource) //mov to location
            val tileBeforeLast = pathToSource[pathToSource.lastIndex]
            Pos(tileBeforeLast.x, tileBeforeLast.y)
        }

        //check if there is already a container for this source
        val containers = source.pos.findInRange<Structure>(FIND_MY_STRUCTURES, sourceToContainerMaxRange).filter { it.structureType == STRUCTURE_CONTAINER }
        println("using container mining with containers $containers")
        when (containers.size) {
            0 -> {
                //if there is a road leading up to the source build the container there
                if (source.room.lookAt(containertile.x, containertile.y).any { it.type == LOOK_CONSTRUCTION_SITES && it.constructionSite!!.structureType == STRUCTURE_CONTAINER }) {
                    return
                }

                val code = source.room.createConstructionSite(containertile.x, containertile.y, STRUCTURE_CONTAINER)
                when (code) {
                    OK -> println("building container for source ${source.id}]")
                    else -> print("error placing construction site for source ${source.id}")
                }

            }
            1 -> {
                val container = containers.single()
                //set target and move to
                if (creep.pos.x != container.pos.x || creep.pos.y != container.pos.y) {
                    creep.moveTo(container.pos) //TODO deal with return
                } else {
                    creep.harvest(source)
                }
            }
            else -> {
                //TODO what?
                println("Error! multiple containers within $sourceToContainerMaxRange range of source ${source.id}")
            }
        }

    }


    fun Creep.requestSource(energySources: Array<Source>): Source? {

        println("usedSourcesWithCreepCounts=$usedSourcesWithCreepCounts")

        //find a source that is close and has some free spots
        energySources.sort({ a, b -> (dist2(this.pos, a.pos) - dist2(this.pos, b.pos)) })

        for (energySource in energySources) {
            if (usedSourcesWithCreepCounts.getOrElse(energySource.id, { 0 }) < MAX_MINER_PER_SOURCE) {
                //assign creep to energy source
                return energySource

            }
        }

        return null
    }

    fun shouldContinueMininig(creep: Creep): Boolean {
        if (creep.name.startsWith(BodyDefinition.BASIC_WORKER.name)) {
            return creep.carry.energy < creep.carryCapacity
        } else if (creep.name.startsWith(BodyDefinition.MINER.name)) {
            return true
        }

        throw IllegalStateException("stop mining not handled")

    }

    fun dist2(from: RoomPosition, to: RoomPosition) =
            (to.x - from.x) * (to.x - from.x) + (to.y - from.y) * (to.y - from.y)


}