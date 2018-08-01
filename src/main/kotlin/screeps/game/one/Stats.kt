package screeps.game.one

import types.base.global.Game
import types.base.global.Memory
import types.base.prototypes.Room

object Stats {

    private var Memory.stats: dynamic
        get() = this.asDynamic().stats
        set(value) = run { this.asDynamic().stats = value }

    private val stats = Memory.stats

    init {
        if (Memory.stats == null) {
            Memory.stats = Any()
        }
    }

    fun write(room: Room) {

        val r = "rooms.${room.name}"

        stats["$r.mine"] = if (room.controller?.my == true) 1 else 0
        stats["$r.energyAvailable"] = room.energyAvailable
        stats["$r.energyCapacityAvailable"] = room.energyCapacityAvailable
        room.storage?.let {
            stats["$r.storage"] = it.store
        }
        room.controller?.let {
            val r = "$r.controller"
            stats["$r.level"] = it.level
            stats["$r.progress"] = it.progress
            stats["$r.progressTotal"] = it.progressTotal
        }
    }

    fun write(key: String, value: Any) {
        Memory.stats[key] = value
    }

    fun tickEnds() {
        stats["cpu.used"] = Game.cpu.getUsed()
        stats["cpu.limit"] = Game.cpu.limit
        stats["cpu.bucket"] = Game.cpu.bucket
    }
}