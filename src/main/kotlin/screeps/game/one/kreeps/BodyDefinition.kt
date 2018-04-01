package screeps.game.one.kreeps

import types.*

enum class BodyDefinition(val bodyType: Array<BodyType>, val maxSize: Int = 0) {
    BASIC_WORKER(arrayOf(WORK, CARRY, MOVE), maxSize = 5),
    MINER(arrayOf(WORK, WORK, MOVE), maxSize = 2),
    MINER_BIG(arrayOf(WORK, WORK, WORK, WORK, WORK, MOVE, MOVE), maxSize = 1), //completely drains a Source
    BIG_WORKER(arrayOf(WORK, WORK, WORK, WORK, CARRY, MOVE, MOVE)),
    HAULER(arrayOf(CARRY, CARRY, MOVE), maxSize = 5),
    SCOUT(arrayOf(MOVE), maxSize = 1);

    fun getCost(): Int = bodyType.sumBy { BODYPART_COST[it] as Int }

    data class Body(val tier: Int, val body: List<BodyType>)

    fun getBiggest(availableEnergy: Int): Body {
        var energyCost = availableEnergy
        val cost = getCost()
        val body = mutableListOf<BodyType>()
        var size = 0

        while (energyCost - cost > 0 && (maxSize == 0 || size < maxSize)) {
            energyCost -= cost
            body.addAll(bodyType)
            size += 1
        }
        body.sort()

        return Body(size, body)
    }

}