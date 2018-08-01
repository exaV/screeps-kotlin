package screeps.game.one

import types.base.global.Game
import types.base.global.Memory
import types.base.prototypes.Room

object Stats {

    private var Memory.stats: dynamic
        get() = this.asDynamic().stats
        set(value) = run { this.asDynamic().stats = value }

    init {
        if (Memory.stats == null) {
            Memory.stats = Any()
        }
    }

    fun write(room: Room) {

        val r = "rooms.${room.name}"

        Memory.stats["$r.mine"] = if (room.controller?.my == true) 1 else 0
        Memory.stats["$r.energyAvailable"] = room.energyAvailable
        Memory.stats["$r.energyCapacityAvailable"] = room.energyCapacityAvailable
        room.storage?.let {
            Memory.stats["$r.storage"] = it.store
        }
        room.controller?.let {
            val r = "$r.controller"
            Memory.stats["$r.level"] = it.level
            Memory.stats["$r.progress"] = it.progress
            Memory.stats["$r.progressTotal"] = it.progressTotal
        }
    }

    fun write(key: String, value: Any) {
        Memory.stats[key] = value
    }

    fun tickEnds() {
        Memory.stats["cpu.used"] = Game.cpu.getUsed()
        Memory.stats["cpu.limit"] = Game.cpu.limit
        Memory.stats["cpu.bucket"] = Game.cpu.bucket
    }
}