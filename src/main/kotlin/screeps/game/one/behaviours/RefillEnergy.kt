package screeps.game.one.behaviours

import screeps.game.one.*
import screeps.game.one.kreeps.BodyDefinition
import types.*

class RefillEnergy {
    companion object {
        const val MAX_MINER_PER_SOURCE = 1
        const val MAX_CREEP_PER_DROPPED_ENERGY = 1
    }

    val droppedEnergyByRoom: MutableMap<Room, Array<Resource>> = mutableMapOf()
    val minersByRoom: MutableMap<Room, Array<Creep>> = mutableMapOf()

    val usedSourcesWithCreepCounts by lazyPerTick {
        Context.creeps
                .map { it.value.memory.assignedEnergySource }
                .filterNotNull()
                .groupingBy { it }
                .eachCount()
                .toMutableMap()
    }

    fun run(creep: Creep) {
        if (creep.name.startsWith(BodyDefinition.MINER.name)) {
            miner(creep)
        } else {
            val canWork = worker(creep)
        }
    }

    private fun worker(creep: Creep): Boolean {
        /*
        workers can be assigned to a
        * miner
        * container
        * source
        * in the future... carry

         */
        if (shouldContinueMininig(creep)) {
            val source: GameObject? = getSourceFromMemory(creep) ?: creep.requestEnergy()

            if (source == null) {
                println("no energy available for worker ${creep.name}")
                return false
            } else {
                creep.memory.assignedEnergySource = source.id
            }

            when (source) {
                is Creep -> refillFromMinerCreep(creep, source)
                is Source -> println("my source is Source")
                is Resource -> refillFromResource(creep, source)
                is StructureContainer -> refillFromContainer(creep, source)
                else -> println("dont know the type of my source")
            }

            return true
        } else {
            creep.memory.state = CreepState.IDLE
            return true
        }
    }

    private fun getSourceFromMemory(creep: Creep): GameObject? {
        val assigned = Game.getObjectById<GameObject>(creep.memory.assignedEnergySource)
        if (assigned == null) {
            creep.memory.assignedEnergySource = null
        }
        return assigned
    }

    private fun refillFromContainer(creep: Creep, source: Structure) {
        when (creep.withdraw(source, RESOURCE_ENERGY)) {
            OK -> kotlin.run { }
            ERR_NOT_IN_RANGE -> creep.moveTo(source.pos, MoveToOpts(visualizePathStyle = Style(stroke = "#ffaa00")))
            ERR_NOT_ENOUGH_RESOURCES -> creep.memory.assignedEnergySource = null
            else -> println("${creep.name} could now withdraw from ${source.id}")
        }
    }

    private fun refillFromResource(creep: Creep, resource: Resource) {
        if (creep.pickup(resource) == ERR_NOT_IN_RANGE) {
            creep.moveTo(resource.pos, MoveToOpts(visualizePathStyle = Style(stroke = "#ffaa00")))
        }
    }

    private fun refillFromMinerCreep(creep: Creep, miner: Creep) {
        require(miner.name.startsWith(BodyDefinition.MINER.name))

        val minerTile = miner.room.lookAt(miner.pos)

        val tileWithResource =
            minerTile.firstOrNull { it.type == LOOK_RESOURCES && it.resource!!.resourceType == RESOURCE_ENERGY }
        if (tileWithResource != null) {
            refillFromResource(creep, tileWithResource.resource!!)
        } else {
            val tileWithContainer =
                minerTile.firstOrNull { it.type == LOOK_STRUCTURES && it.structure!!.structureType == STRUCTURE_CONTAINER }

            if (tileWithContainer != null) {
                refillFromContainer(creep, tileWithContainer.structure!!)
            } else {
                println("assigned miner ${miner.id} is not yet mining")
            }
        }
    }

    private fun Creep.requestEnergy(): GameObject? {

        val droppedEnergy = droppedEnergyByRoom.getOrPut(this.room, {
            val e = this.room.findDroppedEnergy()
            e.sort({ a, b -> b.amount - a.amount })
            e
        })

        val miners = minersByRoom.getOrPut(this.room, {
            Context.creeps.filter { it.key.startsWith(BodyDefinition.MINER.name) && it.value.room.name == this.room.name }
                .values.toTypedArray()
        });

        //find a source that is close and has some free spots
        for (energy in droppedEnergy) {
            if (usedSourcesWithCreepCounts.getOrElse(energy.id, { 0 }) < MAX_CREEP_PER_DROPPED_ENERGY) {
                //assign creep to energy source
                return energy
            }
        }

        //assign to a miner
        if (miners.isNotEmpty()) {
            //biggest miner first
            // TODO this could be bad because usedSourcesWithCreepCounts only updated in the beginning of the tick
            // and many could be assigned to same miner
            return miners.maxBy {
                val creepsAssignedToMiner = usedSourcesWithCreepCounts[it.id] ?: 0
                val minerOutput = it.body.count { it.type == WORK } * 2
                minerOutput.toDouble() / (creepsAssignedToMiner + 1)
            }
        }


        val containers = room.findStructures().filter { it.structureType == STRUCTURE_CONTAINER }
            .filter { (it as StructureContainer).store.energy > 0 }
        if (containers.isNotEmpty()) {
            println("assigning creep $name's energysource to a container")
        }
        return findClosest(containers)
    }

    private fun miner(creep: Creep) {
        if (shouldContinueMininig(creep)) {
            var assignedSource = creep.memory.assignedEnergySource
            if (assignedSource == null) {
                val energySources = creep.room.findEnergy()
                val source = creep.requestSource(energySources)
                if (source == null) {
                    println("no energy sources available for creep ${creep.name} in ${creep.room}")
                    return
                }
                creep.memory.assignedEnergySource = source.id
                assignedSource = source.id
            }

            val source = Game.getObjectById<Source>(assignedSource)
            if (source == null) {
                creep.memory.assignedEnergySource = null
                return
            }

            val useContainerMining =
                creep.room.controller?.level ?: 0 >= 3 && creep.name.startsWith(BodyDefinition.MINER_BIG.name)
            if (useContainerMining) {
                containerMining(creep, source)
            } else {
                val code = creep.harvest(source)
                when (code) {
                    ERR_NOT_IN_RANGE -> {
                        val moveCode = creep.moveTo(source.pos, MoveToOpts())
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
            creep.memory.assignedEnergySource = null
        }
    }

    private fun containerMining(creep: Creep, source: Source) {
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
        val containers = source.pos.findInRange<Structure>(FIND_STRUCTURES, sourceToContainerMaxRange)
            .filter { it.structureType == STRUCTURE_CONTAINER }
        when (containers.size) {
            0 -> {
                if (source.room.lookAt(containertile.x, containertile.y)
                        .any { it.type == LOOK_CONSTRUCTION_SITES && it.constructionSite!!.structureType == STRUCTURE_CONTAINER }
                ) {
                    return
                }

                val code = source.room.createConstructionSite(containertile.x, containertile.y, STRUCTURE_CONTAINER)
                when (code) {
                    OK -> println("building container for source ${source.id}]")
                    else -> println("error placing construction site for source ${source.id}")
                }

            }
            1 -> {
                val container = containers.single()
                //set target and move to
                if (creep.pos.x != container.pos.x || creep.pos.y != container.pos.y) {
                    creep.moveTo(container.pos, MoveToOpts()) //TODO deal with return
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


    private fun Creep.requestSource(energySources: Array<Source>): Source? {

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

    private fun shouldContinueMininig(creep: Creep): Boolean {
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