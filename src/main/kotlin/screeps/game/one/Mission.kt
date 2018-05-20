package screeps.game.one

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import types.base.global.Memory

abstract class Mission(open val parent: Mission? = null) {
    abstract val missionId: String
    abstract fun update();
}


@Serializable
data class MissionMemory(val upgradeMissions: MutableList<UpgradeMissionMemory>)

object Missions {
    val missionMemory: MissionMemory
    val activeMissions: MutableList<Mission> = mutableListOf()

    init {
        missionMemory = Memory.missionMemory ?: MissionMemory(mutableListOf())
    }

    fun load() {
        for (upgrademission in missionMemory.upgradeMissions) {
            if (activeMissions.none { it.missionId == upgrademission.id }) {
                activeMissions.add(RoomUpgradeMission(upgrademission.controllerId))
            }
        }
    }

    fun save() {
        Memory.missionMemory = missionMemory
    }

    private var Memory.missionMemory: MissionMemory?
        get() {
            val internal = this.asDynamic().missionMemory
            return if (internal == null) null else JSON.parse(internal)
        }
        set(value) {
            val stringyfied = if (value == null) null else JSON.stringify(value)
            this.asDynamic().missionMemory = stringyfied
        }
}