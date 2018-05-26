package screeps.game.one

import kotlinx.serialization.Serializable
import screeps.game.one.kreeps.BodyDefinition
import traveler.travelTo
import types.base.global.Game
import types.base.global.OK
import types.base.global.STRUCTURE_SPAWN
import types.base.iterator
import types.base.prototypes.*
import types.base.prototypes.structures.StructureSpawn

class ColonizeMission(val memory: ColonizeMissionMemory) : Mission() {

    companion object {
        private const val MIN_WORKERS = 2
        fun forRoom(room: Room): ColonizeMission {
            val controller = room.controller ?: throw IllegalStateException("Room $room has no controller")
            return forRoom(controller.pos)
        }

        fun forRoom(room: RoomPosition): ColonizeMission {
            val memory = ColonizeMissionMemory(room.x, room.y, room.roomName)
            val mission = ColonizeMission(memory)
            Missions.missionMemory.colonizeMissionMemory.add(memory)
            Missions.activeMissions.add(mission)
            println("spawning persistent ColonizeMission for room ${room.roomName}")

            return mission
        }
    }

    override val missionId: String = memory.missionId
    val pos = RoomPosition(memory.x, memory.y, memory.roomName)

    enum class State {
        SPAWNING_CLAIMER,
        CLAIM,
        DONE_CLAIM,
        SPAWNING_BUILDER,
        BUILD_SPAWN,
        NO_SPAWN_LOCATION,
        DONE
    }

    init {

    }

    var claimerName: String? = null
    val workernames: MutableList<String> = mutableListOf()


    override fun update() {
        when (memory.state) {
            State.SPAWNING_CLAIMER -> {
                val claimer = Context.creeps.values.find {
                    it.memory.missionId == missionId && it.memory.state == CreepState.CLAIM
                }
                if (claimer == null) {
                    requestCreepOnce(BodyDefinition.CLAIMER, KreepSpawnOptions(CreepState.CLAIM, missionId))
                } else {
                    this.claimerName = claimer.name
                    memory.state = State.CLAIM
                }
            }

            State.CLAIM -> {
                if (claimerName == null || claimerName !in Context.creeps) {
                    memory.state = State.SPAWNING_CLAIMER
                    return
                }

                val claimer = Context.creeps[claimerName!!]!!
                if (claimer.pos.inRangeTo(pos, 1)) {
                    if (claimer.room.controller?.my == true) {
                        memory.state == State.BUILD_SPAWN
                        claimer.memory.state = CreepState.IDLE
                        claimer.memory.missionId = null
                    } else {
                        claimer.claimController(claimer.room.controller!!)
                        //claimer.reserveController(claimer.room.controller!!)
                    }
                } else {
                    val res = claimer.travelTo(pos)
                    if (res != OK) {
                        println("claimer could not move to room ${pos.roomName} because of $res")
                    }
                }
            }

            State.SPAWNING_BUILDER -> {
                val workers = Context.creeps.values.filter {
                    it.memory.missionId == missionId && it.name.startsWith(BodyDefinition.BASIC_WORKER.name)
                }

                if (workers.size < MIN_WORKERS) {
                    requestCreepOnce(BodyDefinition.BASIC_WORKER, KreepSpawnOptions(CreepState.REFILL, missionId))
                } else {
                    this.workernames.addAll(workers.map { it.name })
                    memory.state = State.BUILD_SPAWN
                }
            }

            State.BUILD_SPAWN -> {
                if (workernames.count { it in Context.creeps } < MIN_WORKERS) {
                    memory.state = State.SPAWNING_BUILDER
                    workernames.clear()
                }

                for (workerName in workernames) {
                    if (workerName !in Context.creeps) continue
                    val worker = Context.creeps[workerName]!!
                    buildSpawn(worker)
                }
            }

            else -> {
            }
        }

    }

    private fun buildSpawn(worker: Creep) {
        if (worker.carry.energy < worker.carryCapacity) return
        worker.memory.state = CreepState.MISSION

        if (worker.pos.roomName == pos.roomName) {
            if (worker.memory.targetId == null) {
                val constructionSite = findSpawnPosition(Context.rooms[memory.roomName]!!)
                if (constructionSite == null) {
                    if (worker.room.findStructures().any { it is StructureSpawn && it.my }) {
                        memory.state = State.DONE
                        worker.memory.state = CreepState.IDLE
                        worker.memory.missionId = null
                    } else {
                        memory.state = State.NO_SPAWN_LOCATION
                        return
                    }

                } else {
                    worker.memory.targetId = constructionSite.id
                }
            }
            if (worker.memory.state != CreepState.REFILL && worker.memory.state != CreepState.CONSTRUCTING) {
                worker.memory.state = CreepState.CONSTRUCTING
            }
        } else {
            val res = worker.travelTo(pos)
            if (res != OK) {
                println("worker ${worker.name} could not move to room ${pos.roomName} because of $res")
            }
        }
    }

    private fun findSpawnPosition(room: Room): ConstructionSite? {
        val constructionSite = room.findConstructionSites().firstOrNull { it.structureType == STRUCTURE_SPAWN }

        if (constructionSite == null) {
            for ((name, flag) in Game.flags) {
                if (name == "spawn" && flag.pos.roomName == pos.roomName) {
                    room.createConstructionSite(flag.pos, STRUCTURE_SPAWN)
                    return null
                }
            }
            return null
        } else return constructionSite
    }
}

@Serializable
class ColonizeMissionMemory(var x: Int, var y: Int, val roomName: String) : MissionMemory<ColonizeMission>() {

    override val missionId: String
        get() = "colonize_$roomName"

    override fun restoreMission(): ColonizeMission {
        return ColonizeMission(this)
    }

    var state: ColonizeMission.State = ColonizeMission.State.CLAIM
}

sealed class ColonizeSubMission() {
    abstract fun update()
}

class ClaimMission(val claimer: Creep, val position: RoomPosition) : ColonizeSubMission() {
    override fun update() {
        claimer.travelTo(position)
    }
}
