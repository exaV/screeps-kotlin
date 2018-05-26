package screeps.game.one

import kotlinx.serialization.Serializable
import screeps.game.one.kreeps.BodyDefinition
import traveler.travelTo
import types.base.global.Game
import types.base.global.OK
import types.base.global.STRUCTURE_SPAWN
import types.base.iterator
import types.base.prototypes.*

class ColonizeMission(val memory: ColonizeMissionMemory) : Mission() {

    companion object {
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
    var workerName: String? = null

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
                val worker = Context.creeps.values.find {
                    it.memory.missionId == missionId && it.name.startsWith(BodyDefinition.BASIC_WORKER.name)
                }
                if (worker == null) {
                    requestCreepOnce(BodyDefinition.BASIC_WORKER, KreepSpawnOptions(CreepState.REFILL, missionId))
                } else {
                    this.workerName = worker.name
                    memory.state = State.BUILD_SPAWN
                }
            }

            State.BUILD_SPAWN -> {
                println("build spawn with $workerName")

                if (workerName == null || workerName !in Context.creeps) {
                    memory.state = State.SPAWNING_BUILDER
                    return
                }

                val worker = Context.creeps[workerName!!]!!
                worker.say("hey!")
                if (worker.carry.energy < worker.carryCapacity) return
                worker.memory.state = CreepState.MISSION

                println("worker $workerName preparing to move")
                if (worker.pos.roomName == pos.roomName) {
                    if (worker.memory.targetId == null) {
                        val constructionSite = findSpawnPosition(Context.rooms[memory.roomName]!!)
                        if (constructionSite == null) {
                            memory.state = State.NO_SPAWN_LOCATION
                            return
                        }
                        worker.memory.targetId = constructionSite.id
                    }
                    if (worker.memory.state != CreepState.REFILL && worker.memory.state != CreepState.CONSTRUCTING) {
                        worker.memory.state = CreepState.CONSTRUCTING
                    }
                } else {
                    val res = worker.travelTo(pos)
                    if (res != OK) {
                        println("claimer could not move to room ${pos.roomName} because of $res")
                    }
                }

            }

            else -> {
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
