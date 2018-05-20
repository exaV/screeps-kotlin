package screeps.game.one

import kotlinx.serialization.Serializable
import screeps.game.one.kreeps.BodyDefinition
import types.base.global.Game
import types.base.prototypes.Creep
import types.base.prototypes.structures.StructureController

//sealed class UpgradeMission1
//class RoomUpgradeMission : UpgradeMission1()
//sealed class RunningUpgradeMission : UpgradeMission1()
//class EasyUpgradeMission : RunningUpgradeMission()
//class LinkUpgradeMission : RunningUpgradeMission()
//class RCL8UpgradeMission : RunningUpgradeMission()


/**
 * Mission to upgrade a controller using multiple creeps to carry energy
 * Can be cached safely
 *
 * @throws IllegalStateException if it can't be initialized
 */
abstract class UpgradeMission(val controllerId: String) : Mission() {
    val controller: StructureController
    val missionId = controllerId

    init {
        val controllerFromMemory = Game.getObjectById<StructureController>(controllerId)
        controller = controllerFromMemory ?:
                throw IllegalStateException("could not load controller for id $controllerId") // captured
    }

    abstract fun update()
}


class RoomUpgradeMission(controllerId: String) : UpgradeMission(controllerId) {
    enum class State {
        EARLY, LINK, RCL8
    }

    val memory: UpgradeMissionMemory
    var mission: UpgradeMission

    init {
        memory = Missions.missionMemory.upgradeMissions.find { it.controllerId == controllerId } ?:
                UpgradeMissionMemory(controllerId, State.EARLY)

        @Suppress("WhenWithOnlyElse")
        when (memory.state) {
            else -> mission = EarlyGameUpgradeMission(this, controllerId, if (controller.level == 8) 1 else 3)
        }
    }

    override fun update() {
        mission.update()
    }
}

class EarlyGameUpgradeMission(
    override val parent: UpgradeMission,
    controllerId: String,
    private val minWorkerCount: Int = 3
) : UpgradeMission(controllerId) {

    private val workers: MutableList<Creep> = mutableListOf()

    init {
        workers.addAll(Context.creeps.values.filter { it.memory.missionId == parent.missionId })
    }

    override fun update() {

        if (workers.size < minWorkerCount) {
            workers.clear()
            workers.addAll(Context.creeps.values.filter { it.memory.missionId == parent.missionId })

            if (workers.size < minWorkerCount) {
                // check if there are any that are not yet assigned to us
                requestCreep(BodyDefinition.BASIC_WORKER, KreepSpawnOptions(CreepState.UPGRADING, missionId))
            }
        }

        for (worker in workers) {
            if (worker.memory.state == CreepState.IDLE) {
                worker.memory.state = CreepState.UPGRADING
                worker.memory.targetId = controllerId
            }
        }
    }
}

@Serializable
class UpgradeMissionMemory(val controllerId: String, var state: RoomUpgradeMission.State)
