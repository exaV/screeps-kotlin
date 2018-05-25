package screeps.game.one

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import types.base.global.Memory

abstract class Mission(open val parent: Mission? = null) {
    abstract val missionId: String
    abstract fun update();
}


@Serializable
data class ActiveMissionMemory(val upgradeMissions: MutableList<UpgradeMissionMemory>)

object Missions {
    val missionMemory: ActiveMissionMemory
    val activeMissions: MutableList<Mission> = mutableListOf()

    init {
        missionMemory = Memory.activeMissionMemory ?: ActiveMissionMemory(mutableListOf())
    }

    fun load() {
        for (upgrademission in missionMemory.upgradeMissions) {
            if (activeMissions.none { it.missionId == upgrademission.id }) {
                activeMissions.add(RoomUpgradeMission(upgrademission.controllerId))
            }
        }
    }

    fun save() {
        Memory.activeMissionMemory = missionMemory
    }

    private var Memory.activeMissionMemory: ActiveMissionMemory?
        get() {
            val internal = this.asDynamic().missionMemory
            return if (internal == null) null else JSON.parse(internal)
        }
        set(value) {
            val stringyfied = if (value == null) null else JSON.stringify(value)
            this.asDynamic().missionMemory = stringyfied
        }
}