@file:Suppress(
    "INTERFACE_WITH_SUPERCLASS",
    "OVERRIDING_FINAL_MEMBER",
    "RETURN_TYPE_MISMATCH_ON_OVERRIDE",
    "CONFLICTING_OVERLOADS",
    "EXTERNAL_DELEGATION",
    "NESTED_CLASS_IN_EXTERNAL_INTERFACE"
)

package traveler

import types.base.global.ScreepsReturnCode
import types.base.prototypes.RoomPosition

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
    var ignoreRoads: Boolean? get() = definedExternally; set(value) = definedExternally
    var ignoreCreeps: Boolean? get() = definedExternally; set(value) = definedExternally
    var ignoreStructures: Boolean? get() = definedExternally; set(value) = definedExternally
    var preferHighway: Boolean? get() = definedExternally; set(value) = definedExternally
    var highwayBias: Number? get() = definedExternally; set(value) = definedExternally
    var allowHostile: Boolean? get() = definedExternally; set(value) = definedExternally
    var allowSK: Boolean? get() = definedExternally; set(value) = definedExternally
    var range: Number? get() = definedExternally; set(value) = definedExternally
    //    var obstacles: Array<`T$0`>? get() = definedExternally; set(value) = definedExternally
//    var roomCallback: ((roomName: String, matrix: CostMatrix) -> dynamic /* CostMatrix | Boolean */)? get() = definedExternally; set(value) = definedExternally
    var routeCallback: ((roomName: String) -> Number)? get() = definedExternally; set(value) = definedExternally
    var returnData: TravelToReturnData? get() = definedExternally; set(value) = definedExternally
    var restrictDistance: Number? get() = definedExternally; set(value) = definedExternally
    var useFindRoute: Boolean? get() = definedExternally; set(value) = definedExternally
    var maxOps: Number? get() = definedExternally; set(value) = definedExternally
    var movingTarget: Boolean? get() = definedExternally; set(value) = definedExternally
    var freshMatrix: Boolean? get() = definedExternally; set(value) = definedExternally
    var offRoad: Boolean? get() = definedExternally; set(value) = definedExternally
    var stuckValue: Number? get() = definedExternally; set(value) = definedExternally
    var maxRooms: Number? get() = definedExternally; set(value) = definedExternally
    var repath: Number? get() = definedExternally; set(value) = definedExternally
    //    var route: `T$1`? get() = definedExternally; set(value) = definedExternally
    var ensurePath: Boolean? get() = definedExternally; set(value) = definedExternally
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
