package screeps.game.one.kreeps

import types.base.get
import types.base.global.*

enum class BodyDefinition(val bodyPartConstant: Array<BodyPartConstant>, val maxSize: Int = 0) {
    BASIC_WORKER(arrayOf(WORK, CARRY, MOVE), maxSize = 5),
    MINER(arrayOf(WORK, WORK, MOVE), maxSize = 2),
    MINER_BIG(
        arrayOf(
            WORK,
            WORK,
            WORK,
            WORK,
            WORK,
            MOVE,
            MOVE
        ), maxSize = 1
    ), //completely drains a Source
    BIG_WORKER(
        arrayOf(
            WORK,
            WORK,
            WORK,
            WORK,
            CARRY,
            MOVE,
            MOVE
        )
    ),
    HAULER(arrayOf(CARRY, CARRY, MOVE), maxSize = 5),
    SCOUT(arrayOf(MOVE), maxSize = 1);

    val cost: Int
        get() = bodyPartConstant.sumBy { BODYPART_COST[it] }

    data class Body(val tier: Int, val body: List<BodyPartConstant>)

    fun getBiggest(availableEnergy: Int): Body {
        var energyCost = availableEnergy
        val cost = cost
        val body = mutableListOf<BodyPartConstant>()
        var size = 0

        while (energyCost - cost >= 0 && (maxSize == 0 || size < maxSize)) {
            energyCost -= cost
            body.addAll(bodyPartConstant)
            size += 1
        }
        body.sortBy { it.stringValue() }

        return Body(size, body)
    }

}