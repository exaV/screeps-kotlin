package screeps.game.one

import screeps.game.one.kreeps.BodyDefinition
import screeps.game.one.kreeps.CreepSpawnOptions
import types.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun StructureSpawn.spawn(bodyDefinition: BodyDefinition) {
    if (room.energyAvailable < bodyDefinition.getCost()) return

    val body = bodyDefinition.getBiggest(room.energyAvailable)
    val newName = "${bodyDefinition.name}${body.size}_${Game.time}"

    println("attempting to spawn $newName with $body")

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

/**
 * Lazy property that computed at most once per tick
 */
private class TickLazy<T>(val tickInitializer: () -> Map<String, T>) : ReadOnlyProperty<Context, Map<String, T>> {
    var map: Map<String, T> = emptyMap()
    var tick: Number = 0

    override fun getValue(thisRef: Context, property: KProperty<*>): Map<String, T> {
        val currentTick = Game.time

        if (Game.time != tick) {
            tick = currentTick
            map = tickInitializer()
        }
        return this.map
    }
}

/**
 * Creates a lazy property that computed at most once per tick
 */
fun <T> tickLazy(initializer: () -> Map<String, T>): ReadOnlyProperty<Context, Map<String, T>> {
    return TickLazy(initializer)
}