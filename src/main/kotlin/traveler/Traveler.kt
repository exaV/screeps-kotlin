@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "EXTERNAL_DELEGATION",
    "NESTED_CLASS_IN_EXTERNAL_INTERFACE"
)

package traveler

import screeps.api.PathFinder
import screeps.api.RoomPosition
import screeps.api.ScreepsReturnCode

external interface PathfinderReturn {
    var path: Array<RoomPosition>
    var ops: Number
    var cost: Number
    var incomplete: Boolean
}

external interface TravelToReturnData {
    var nextPos: RoomPosition? get() = definedExternally; set(value) = definedExternally
    var pathfinderReturn: PathfinderReturn? get() = definedExternally; set(value) = definedExternally
    var state: TravelState? get() = definedExternally; set(value) = definedExternally
    var path: String? get() = definedExternally; set(value) = definedExternally
}


external interface TravelToOptions {

    /**
     * Creeps won't prefer roads above plains (will still prefer them to swamps). Default is false.
     */
    var ignoreRoads: Boolean? get() = definedExternally; set(value) = definedExternally

    /**
     * Will not path around other creeps. Default is true.
     */
    var ignoreCreeps: Boolean? get() = definedExternally; set(value) = definedExternally

    /**
     * Will not path around structures. Default is false.
     */
    var ignoreStructures: Boolean? get() = definedExternally; set(value) = definedExternally

    /**
     * Creep prefer to travel along highway (empty rooms in between sectors). Default is false.
     */
    var preferHighway: Boolean? get() = definedExternally; set(value) = definedExternally

    var highwayBias: Number? get() = definedExternally; set(value) = definedExternally

    /**
     * Hostile rooms will be included in path. Default is false.
     */
    var allowHostile: Boolean? get() = definedExternally; set(value) = definedExternally
    /**
     * SourceKeeper rooms will be included in path. (if false, SK rooms will still be taken if they are they only viable path).
     * Default is false.
     */
    var allowSK: Boolean? get() = definedExternally; set(value) = definedExternally
    /**
     * Range to goal before it is considered reached. The default is 1.
     */
    var range: Number? get() = definedExternally; set(value) = definedExternally

    /**
     * Array of objects with property {pos: RoomPosition} that represent positions to avoid.
     */
    var obstacles: Array<RoomPosition>

    /**
     * Callback function that accepts two arguments, roomName (string) and matrix (CostMatrix) and returns a CostMatrix or boolean.
     * Used for overriding the default PathFinder callback.
     * If it returns false, that room will be excluded. If it returns a matrix, it will be used in place of the default matrix.
     * If it returns undefined the default matrix will be used instead.
     */
    var roomCallback: ((roomName: String, matrix: PathFinder.CostMatrix) -> dynamic /* CostMatrix | Boolean */)? get() = definedExternally; set(value) = definedExternally

    /**
     * Callback function that accepts one argument, roomName (string) and returns a number representing the foundRoute value that roomName.
     * Used for overriding the findRoute callback.
     * If it returns a number that value will be used to influence the route. If it returns undefined it will use the default value.
     */
    var routeCallback: ((roomName: String) -> Number)? get() = definedExternally; set(value) = definedExternally

    /**
     * If an empty object literal is provided, the RoomPosition being moved to will be assigned to returnData.nextPos.
     */
    var returnData: TravelToReturnData? get() = definedExternally; set(value) = definedExternally

    /**
     * Limits the range the findRoute will search. Default is 32.
     */
    var restrictDistance: Number? get() = definedExternally; set(value) = definedExternally

    /**
     * Can be used to force or prohibit the use of findRoute.
     * If undefined it will use findRoute only for paths that span a larger number of rooms (linear distance >2).
     */
    var useFindRoute: Boolean? get() = definedExternally; set(value) = definedExternally

    /**
     * Limits the ops (CPU) that PathFinder will use. Default is 20000. (~20 CPU)
     */
    var maxOps: Number? get() = definedExternally; set(value) = definedExternally

    /**
     * Allows you to avoid making a new pathfinding call when your destination is only 1 position away from what it was previously.
     * The new direction is just added to the path. Default is false.
     */
    var movingTarget: Boolean? get() = definedExternally; set(value) = definedExternally

    /**
     * Will guarantee that a new structure matrix is generated.
     * This might be necessary if structures are likely to change often. Default is false.
     */
    var freshMatrix: Boolean

    /**
     * Creeps won't prefer plains or roads over swamps, all costs will be 1. Default is false.
     */
    var offRoad: Boolean? get() = definedExternally; set(value) = definedExternally

    /**
     * Number of ticks of non-movement before a creep considers itself stuck. Default is 2.
     */
    var stuckValue: Int? get() = definedExternally; set(value) = definedExternally

    /**
     * Limit how many rooms can be searched by PathFinder. Default is undefined.
     */
    var maxRooms: Number? get() = definedExternally; set(value) = definedExternally

    /**
     * Float value between 0 and 1 representing the probability that creep will randomly invalidate its current path.
     * Setting it to 1 would cause the creep to repath every tick. Default is undefined.
     */
    var repath: Double? get() = definedExternally; set(value) = definedExternally

    /**
     * Supply the route to be used by PathFinder. Default is undefined.
     */
    var route: Any?

    /**
     * This can improve the chance of finding a path in certain edge cases where might otherwise fail.
     * Default is false.
     */
    val ensurePath: Boolean
}

external interface TravelData {
    var state: Array<Any>
    var path: String
}

external interface TravelState {
    var stuckCount: Number
    var lastCoord: Coord
    var destination: RoomPosition
    var cpu: Number
}

external interface TravelerCreep {
    fun travelTo(destination: HasPos, ops: TravelToOptions? = definedExternally /* null */): ScreepsReturnCode
    fun travelTo(destination: RoomPosition, ops: TravelToOptions? = definedExternally /* null */): ScreepsReturnCode
}

external interface Coord {
    var x: Number
    var y: Number
}

external interface HasPos {
    var pos: RoomPosition
}
