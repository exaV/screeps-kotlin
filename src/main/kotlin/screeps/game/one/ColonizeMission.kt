package screeps.game.one

import kotlinx.serialization.Serializable
import screeps.game.one.kreeps.BodyDefinition
import traveler.travelTo
import types.base.global.OK
import types.base.prototypes.Creep
import types.base.prototypes.Room
import types.base.prototypes.RoomPosition

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
        DONE
    }

    init {

    }

    var claimerName: String? = null

    override fun update() {
        when (memory.state) {
            State.SPAWNING_CLAIMER -> {
                val claimer = Context.creeps.values.find { it.memory.missionId == missionId && it.ticksToLive > 5 }
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
                } else {
                    val claimer = Context.creeps[claimerName!!]!!
                    if (claimer.pos.inRangeTo(pos, 1)) {
                        if (claimer.room.controller?.my == true) {
                            memory.state == State.DONE
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
            }

            else -> {
            }
        }

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
