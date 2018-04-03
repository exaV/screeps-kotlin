package types.abstractions

import types.base.global.Game
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Lazy property that computed at most once per tick
 */
private class TickLazy<in P : Any?, T>(val computeOncePerTick: P.() -> T) : ReadOnlyProperty<P, T> {
    var value: T? = null
    var tick: Number = -1

    override fun getValue(thisRef: P, property: KProperty<*>): T {
        val currentTick = Game.time

        if (Game.time != tick) {
            tick = currentTick
            value = computeOncePerTick(thisRef)
        }
        return value!!
    }
}

/**
 * Creates a lazy property that computed at most once per tick
 */
fun <P : Any?, T> lazyPerTick(computeOncePerTick: P.() -> T): ReadOnlyProperty<P, T> {
    return TickLazy(computeOncePerTick)
}