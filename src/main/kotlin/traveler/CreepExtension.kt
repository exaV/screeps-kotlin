package traveler

import screeps.api.Creep
import screeps.api.RoomPosition
import screeps.api.ScreepsReturnCode

/**
 * Use Traveler to travel to target destination.
 */
fun Creep.travelTo(target: RoomPosition, travelToOptions: TravelToOptions? = null): ScreepsReturnCode {
    return if (travelToOptions == null) {
        (this.unsafeCast<TravelerCreep>()).travelTo(target)
    } else {
        (this.unsafeCast<TravelerCreep>()).travelTo(target, travelToOptions)
    }
}