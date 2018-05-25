package screeps.game.one

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import types.base.global.Memory

abstract class Mission(open val parent: Mission? = null) {
    abstract val missionId: String
    abstract fun update()
}


@Serializable
data class ActiveMissionMemory(val missions: MutableList<MissionMemory<out Mission>>)

object Missions {
    val missionMemory: ActiveMissionMemory
    val activeMissions: MutableList<Mission> = mutableListOf()

    init {
        missionMemory = Memory.activeMissionMemory ?: ActiveMissionMemory(mutableListOf())
    }

    fun load() {
        for (memory in missionMemory.missions) {
            if (activeMissions.none { it.missionId == memory.missionId }) {
                activeMissions.add(memory.restoreMission())
            }
        }
    }

    fun save() {
        Memory.activeMissionMemory = missionMemory
    }

    private var Memory.activeMissionMemory: ActiveMissionMemory?
        get() {
            val internal = this.asDynamic()._missionMemory
            return if (internal == null) null else JSON.parse(internal)
        }
        set(value) {
            val stringyfied = if (value == null) null else JSON.stringify(value)
            this.asDynamic()._missionMemory = stringyfied
        }
}

abstract class MissionMemory<T : Mission> {
    abstract val missionId: String
    abstract fun restoreMission(): T
}