package screeps.game.one.kreeps

import types.BodyType
import types.CARRY
import types.MOVE
import types.WORK

enum class BodyDefinition(val bodyType: Array<BodyType>) {
    BASIC_WORKER(arrayOf(WORK, CARRY, MOVE)),
    BIG_WORKER(arrayOf(WORK, WORK, WORK, WORK, CARRY, MOVE, MOVE))
}