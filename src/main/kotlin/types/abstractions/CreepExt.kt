package types.abstractions

import types.Creep
import types.RoomPosition
import types.ScreepsReturnCode
import types.traveler.TravelToOptions
import types.traveler.TravelerCreep

/**
 * Use Traveler to travel to target destination.
 */
fun Creep.travelTo(target: RoomPosition, travelToOptions: TravelToOptions? = null): ScreepsReturnCode {
    return if (travelToOptions == null) {
        (this as TravelerCreep).travelTo(target)
    } else {
        (this as TravelerCreep).travelTo(target, travelToOptions)
    }
}