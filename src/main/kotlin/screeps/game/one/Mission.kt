package screeps.game.one

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import kotlinx.serialization.stringify
import screeps.api.Memory

abstract class Mission(open val parent: Mission? = null) {
    abstract val missionId: String
    abstract fun update()

    open var complete = false
        protected set
}


@Serializable
data class ActiveMissionMemory(
    /*
    Rightnow there is no polymorphic serializer for kotlin-js so we have to resort to this
     */
    val upgradeMissionMemory: MutableList<UpgradeMissionMemory> = mutableListOf(),
    val colonizeMissionMemory: MutableList<ColonizeMissionMemory> = mutableListOf()
)

object Missions {
    val missionMemory: ActiveMissionMemory
    val activeMissions: MutableList<Mission> = mutableListOf()

    init {
        missionMemory = Memory.activeMissionMemory ?: ActiveMissionMemory(mutableListOf())
    }

    fun load() = profiled("missions.load") {
        for (memory in missionMemory.upgradeMissionMemory) {
            if (activeMissions.none { it.missionId == memory.missionId }) {
                activeMissions.add(memory.restoreMission())
            }
        }
        for (memory in missionMemory.colonizeMissionMemory) {
            if (activeMissions.none { it.missionId == memory.missionId }) {
                activeMissions.add(memory.restoreMission())
            }
        }
    }

    fun update() = profiled("missions.update") {
        for (mission in activeMissions) {
            mission.update()
        }
        activeMissions.removeAll { it.complete }
        missionMemory.colonizeMissionMemory.removeAll { it.isComplete() }
    }

    fun save() = profiled("missions.save") {
        Memory.activeMissionMemory = missionMemory
    }

    @UseExperimental(ImplicitReflectionSerializer::class)
    private var Memory.activeMissionMemory: ActiveMissionMemory?
        get() {
            val internal = this.asDynamic()._missionMemory
            return if (internal == null) null else Json.parse(internal)
        }
        set(value) {
            val stringyfied = if (value == null) null else Json.stringify(value)
            this.asDynamic()._missionMemory = stringyfied
        }
}

abstract class MissionMemory<T : Mission> {
    abstract val missionId: String
    abstract fun restoreMission(): T

    open fun isComplete(): Boolean = false
}