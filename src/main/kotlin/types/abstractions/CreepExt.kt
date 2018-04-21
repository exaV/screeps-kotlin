package types.abstractions

import types.Creep
import types.MoveToOpts
import types.RoomPosition
import types.ScreepsReturnCode
import types.traveler.TravelerCreep

/**
 * Use Traveler to travel to target destination.
 */
fun Creep.travelTo(target: RoomPosition, moveToOpts: MoveToOpts = MoveToOpts()): ScreepsReturnCode {
    return (this as TravelerCreep).travelTo(target)
}