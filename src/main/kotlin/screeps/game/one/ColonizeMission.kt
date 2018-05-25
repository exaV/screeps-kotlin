package screeps.game.one

import kotlinx.serialization.Serializable
import types.base.prototypes.Room
import types.base.prototypes.RoomPosition

class ColonizeMission(val memory: ColonizeMissionMemory) : Mission() {

    companion object {
        fun forRoom(room: Room): ColonizeMission {
            val controller = room.controller ?: throw IllegalStateException("Room $room has no controller")
            return forRoom(controller.pos)
        }

        fun forRoom(room: RoomPosition): ColonizeMission {
            return ColonizeMission(ColonizeMissionMemory(room.x, room.y, room.roomName))
        }
    }

    override val missionId: String = memory.id
    val pos = RoomPosition(memory.x, memory.y, memory.roomName)

    init {

    }

    override fun update() {

    }
}

@Serializable
class ColonizeMissionMemory(val x: Int, val y: Int, val roomName: String) : MissionMemory<ColonizeMission>() {

    val id: String
        get() = "colonize_$roomName"

    override fun restore(): ColonizeMission {
        return ColonizeMission(this)
    }
}

abstract class MissionMemory<T : Mission> {
    abstract fun restore(): T
}