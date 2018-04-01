package screeps.game.one

import screeps.game.one.kreeps.BodyDefinition
import screeps.game.one.kreeps.CreepSpawnOptions
import types.*
import types.base.global.Game
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun StructureSpawn.spawn(bodyDefinition: BodyDefinition) {
    if (room.energyAvailable < bodyDefinition.getCost()) return

    val body = bodyDefinition.getBiggest(room.energyAvailable)
    val newName = "${bodyDefinition.name}_T${body.tier}_${Game.time}"

    val code = this.spawnCreep(body.body.toTypedArray(), newName, CreepSpawnOptions(CreepState.REFILL))
    when (code) {
        OK -> println("spawning $newName with body $body")
        ERR_NOT_ENOUGH_ENERGY, ERR_BUSY -> run { } // do nothing
        else -> console.log("unhandled error code $code")
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

/**
 * Lazy property that computed at most once per tick
 */
private class TickLazy<T>(val computeOncePerTick: () -> T) : ReadOnlyProperty<Any?, T> {
    var value: T? = null
    var tick: Number = -1

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val currentTick = Game.time

        if (Game.time != tick) {
            tick = currentTick
            value = computeOncePerTick()
        }
        return value!!
    }
}

/**
 * Creates a lazy property that computed at most once per tick
 */
fun <T> lazyPerTick(computeOncePerTick: () -> T): ReadOnlyProperty<Any?, T> {
    return TickLazy(computeOncePerTick)
}