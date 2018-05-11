package screeps.game.one

import screeps.game.one.kreeps.BodyDefinition
import screeps.game.one.kreeps.KreepSpawnOptions
import types.base.global.ERR_BUSY
import types.base.global.ERR_NOT_ENOUGH_ENERGY
import types.base.global.Game
import types.base.global.OK
import types.base.prototypes.structures.StructureSpawn

fun StructureSpawn.spawn(bodyDefinition: BodyDefinition, spawnOptions: KreepSpawnOptions? = null) {
    if (room.energyAvailable < bodyDefinition.cost) return

    val body = bodyDefinition.getBiggest(room.energyAvailable)
    val newName = "${bodyDefinition.name}_T${body.tier}_${Game.time}"

    val code = this.spawnCreep(body.body.toTypedArray(), newName, spawnOptions ?: KreepSpawnOptions(CreepState.REFILL))
    when (code) {
        OK -> println("spawning $newName with body $body")
        ERR_NOT_ENOUGH_ENERGY, ERR_BUSY -> run { } // do nothing
        else -> throw IllegalArgumentException("error code $code when spawning $newName with body $body")
    }
}


fun requestCreep(bodyDefinition: BodyDefinition, spawnOptions: KreepSpawnOptions) {

}