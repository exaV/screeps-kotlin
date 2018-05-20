package screeps.game.one

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import types.base.global.Memory

abstract class Mission(open val parent: Mission? = null) {

}


@Serializable
data class MissionMemory(val upgradeMissions: MutableList<UpgradeMissionMemory>)

object Missions {
    val missionMemory: MissionMemory

    init {
        missionMemory = Memory.missionMemory ?: MissionMemory(mutableListOf())
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
            println("missionMemory=$stringyfied")
            this.asDynamic().missionMemory = stringyfied
        }
}