package screeps.game.one.kreeps

import types.*

enum class BodyDefinition(val bodyType: Array<BodyType>) {
    BASIC_WORKER(arrayOf(WORK, CARRY, MOVE)),
    MINER(arrayOf(WORK, WORK, MOVE)),
    BIG_WORKER(arrayOf(WORK, WORK, WORK, WORK, CARRY, MOVE, MOVE));

    fun getCost(): Int = bodyType.sumBy { Cost[it] }
    fun getBiggest(availableEnergy: Int): List<BodyType> {
        var energyCost = availableEnergy
        val cost = getCost()
        val body = mutableListOf<BodyType>()

        while (energyCost - cost > 0) {
            energyCost -= cost
            body.addAll(bodyType)
        }

        return body

    }

}


object Cost {
    operator fun get(value: BodyType): Int {
        return BODYPART_COST[value] as Int
    }
}