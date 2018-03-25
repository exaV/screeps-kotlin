package screeps.game.one.kreeps

import types.*

enum class BodyDefinition(val bodyType: Array<BodyType>) {
    BASIC_WORKER(arrayOf(WORK, CARRY, MOVE)),
    BIG_WORKER(arrayOf(WORK, WORK, WORK, WORK, CARRY, MOVE, MOVE));

    fun getCost(): Int = bodyType.sumBy { Cost[it] }


}


object Cost {
    operator fun get(value: BodyType): Int {
        return BODYPART_COST[value] as Int
    }
}