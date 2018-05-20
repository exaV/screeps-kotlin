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
abstract class UpgradeMission(controllerId: String) : Mission() {
    override val id = "upgrade_$controllerId"
    val controller: StructureController
    val missionId = controllerId

    init {
        val controllerFromMemory = Game.getObjectById<StructureController>(controllerId)
        controller = controllerFromMemory ?:
                throw IllegalStateException("could not load controller for controllerId $controllerId") // captured
    }
}


class RoomUpgradeMission(controllerId: String) : UpgradeMission(controllerId) {

    enum class State {
        EARLY, LINK, RCL8_MAINTENANCE, RCL8_IDLE
    }

    val memory: UpgradeMissionMemory
    var mission: UpgradeMission

    init {
        memory = Missions.missionMemory.upgradeMissions.find { it.id == id } ?:
                UpgradeMissionMemory(id, controllerId, State.EARLY)

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
    private val minWorkerCount: Int
) : UpgradeMission(controllerId) {

    private val workers: MutableList<Creep> = mutableListOf()

    init {
        workers.addAll(Context.creeps.values.filter { it.memory.missionId == missionId })
    }

    override fun update() {

        if (workers.size < minWorkerCount) {
            workers.clear()
            workers.addAll(Context.creeps.values.filter { it.memory.missionId == missionId })

            if (workers.size < minWorkerCount
                && workers.size + GlobalSpawnQueue.spawnQueue.count { it.spawnOptions.missionId == missionId } < minWorkerCount
            ) {
                requestCreep(BodyDefinition.BASIC_WORKER, KreepSpawnOptions(CreepState.UPGRADING, missionId))
                println("requested creep for mission EarlyGameUpgradeMission in room ${controller.room}")
            }
        }

        for (worker in workers) {
            if (worker.memory.state == CreepState.IDLE) {
                worker.memory.state = CreepState.UPGRADING
                worker.memory.targetId = controller.id
            }
        }
    }
}

@Serializable
class UpgradeMissionMemory(val id: String, val controllerId: String, var state: RoomUpgradeMission.State)
