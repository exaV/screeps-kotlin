package screeps.game.one

import screeps.game.one.kreeps.BodyDefinition
import screeps.game.one.kreeps.CreepSpawnOptions
import types.*

fun StructureSpawn.spawn(bodyDefinition: BodyDefinition) {
    val body = bodyDefinition.getBiggest(this.energy)
    val newName = "${bodyDefinition.name}${body.size}_${Game.time}"


    val code = this.spawnCreep(body.toTypedArray(), newName, CreepSpawnOptions(CreepState.REFILL))
    when (code) {
        OK -> println("spawning $newName with body $body")
        ERR_BUSY -> console.log("busy")
        ERR_NOT_ENOUGH_ENERGY -> run { } // do nothing
        else -> console.log("unhandled error code $code")
    }
}

fun <T : RoomObject> Creep.findClosest(roomObjects: Collection<T>): T? {

    var closest: T? = null
    var minDistance = Double.MAX_VALUE
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
    var minDistance = Double.MAX_VALUE
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