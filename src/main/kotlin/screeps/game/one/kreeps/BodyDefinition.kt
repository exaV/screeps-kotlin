package screeps.game.one.kreeps

import types.*

enum class BodyDefinition(val bodyType: Array<BodyType>, val maxSize: Int = 0) {
    BASIC_WORKER(arrayOf(WORK, CARRY, MOVE), maxSize = 3),
    MINER(arrayOf(WORK, WORK, MOVE), maxSize = 2),
    MINER_BIG(arrayOf(WORK, WORK, WORK, WORK, WORK, MOVE, MOVE), maxSize = 1),
    BIG_WORKER(arrayOf(WORK, WORK, WORK, WORK, CARRY, MOVE, MOVE)),
    HAULER(arrayOf(CARRY, CARRY, MOVE));

    fun getCost(): Int = bodyType.sumBy { Cost[it] }
    fun getBiggest(availableEnergy: Int): List<BodyType> {
        var energyCost = availableEnergy
        val cost = getCost()
        val body = mutableListOf<BodyType>()
        var size = 0

        while (energyCost - cost > 0 && (maxSize == 0 || size < maxSize)) {
            energyCost -= cost
            body.addAll(bodyType)
            size += 1
        }

        return body
    }

}


object Cost {
    operator fun get(value: BodyType): Int {
        return BODYPART_COST[value] as Int
    }
}