package types.abstractions

import types.Creep
import types.MoveToOpts
import types.RoomPosition
import types.ScreepsReturnCode

fun Creep.travelTo(target: RoomPosition, moveToOpts: MoveToOpts = MoveToOpts()): ScreepsReturnCode {
    return moveTo(target, moveToOpts)
}