package screeps.game.one

import screeps.game.one.kreeps.BodyDefinition
import screeps.game.one.kreeps.KreepSpawnOptions
import traveler.travelTo
import types.base.global.ERR_NOT_IN_RANGE
import types.base.global.Game
import types.base.global.Memory
import types.base.prototypes.Creep
import types.base.prototypes.structures.StructureController

class Missions(private val backing: MutableList<String>) : MutableList<String> by backing {

}

var Memory.missions: Missions
    get() = JSON.parse(this.asDynamic().missions)
    set(value) {
        this.asDynamic().missions = value
    }


/**
 * Mission to upgrade a controller using multiple creeps to carry energy
 * Can be cached safely
 *
 * @throws IllegalStateException if it can't be initialized
 */
class UpgradeMission(val controllerId: String) {
    private val controller: StructureController
    private val missionId = controllerId
    private val minWorkerCount = 3
    private val workers: MutableList<Creep> = mutableListOf()

    init {

        val controllerFromMemory = Game.getObjectById<StructureController>(controllerId)
        controller = controllerFromMemory ?:
                throw IllegalStateException("could not load controller for id $controllerId") // captured

        workers.addAll(Context.creeps.values.filter { it.memory.missionId == missionId })
    }


    fun execute() {

        if (workers.size < minWorkerCount) {
            workers.clear()
            workers.addAll(Context.creeps.values.filter { it.memory.missionId == missionId })

            if (workers.size < minWorkerCount) {
                // check if there are any that are not yet assigned to us
                requestCreep(BodyDefinition.BASIC_WORKER, KreepSpawnOptions(CreepState.UPGRADING, missionId))
            }
        }

        for (worker in workers) {
            if (worker.upgradeController(controller) == ERR_NOT_IN_RANGE) {
                worker.travelTo(controller.pos)
            }
        }

    }


}

