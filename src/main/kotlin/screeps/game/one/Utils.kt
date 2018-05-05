package screeps.game.one

import screeps.game.one.kreeps.BodyDefinition
import screeps.game.one.kreeps.KreepSpawnOptions
import types.base.global.ERR_BUSY
import types.base.global.ERR_NOT_ENOUGH_ENERGY
import types.base.global.Game
import types.base.global.OK
import types.base.prototypes.Creep
import types.base.prototypes.RoomObject
import types.base.prototypes.structures.StructureSpawn

fun StructureSpawn.spawn(bodyDefinition: BodyDefinition) {
    if (room.energyAvailable <= bodyDefinition.cost) return

    val body = bodyDefinition.getBiggest(room.energyAvailable)
    val newName = "${bodyDefinition.name}_T${body.tier}_${Game.time}"

    val code = this.spawnCreep(body.body.toTypedArray(), newName, KreepSpawnOptions(CreepState.REFILL))
    when (code) {
        OK -> println("spawning $newName with body $body")
        ERR_NOT_ENOUGH_ENERGY, ERR_BUSY -> run { } // do nothing
        else -> throw IllegalArgumentException("error code $code when spawning $newName with body $body")
    }
}

fun <T : RoomObject> Creep.findClosest(roomObjects: Collection<T>): T? {

    var closest: T? = null
    var minDistance = Int.MAX_VALUE
    for (roomObject in roomObjects) {
        val dist = (roomObject.pos.x - this.pos.x) * (roomObject.pos.x - this.pos.x) +
                (roomObject.pos.y - this.pos.y) * (roomObject.pos.y - this.pos.y)

        if (dist < minDistance) {
            minDistance = dist
            closest = roomObject
        }
    }
    return closest
}

fun <T : RoomObject> Creep.findClosest(roomObjects: Array<out T>): T? {
    var closest: T? = null
    var minDistance = Int.MAX_VALUE
    for (roomObject in roomObjects) {
        val dist = (roomObject.pos.x - this.pos.x) * (roomObject.pos.x - this.pos.x) +
                (roomObject.pos.y - this.pos.y) * (roomObject.pos.y - this.pos.y)

        if (dist < minDistance) {
            minDistance = dist
            closest = roomObject
        }
    }
    return closest
}

fun <T : RoomObject> Creep.findClosestNotEmpty(roomObjects: Array<out T>): T {
    require(roomObjects.isNotEmpty())
    return findClosest(roomObjects)!!
}