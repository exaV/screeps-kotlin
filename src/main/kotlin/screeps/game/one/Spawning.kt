package screeps.game.one

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import screeps.game.one.kreeps.BodyDefinition
import screeps.game.one.kreeps.KreepSpawnOptions
import types.base.global.*
import types.base.prototypes.structures.StructureSpawn

fun StructureSpawn.spawn(bodyDefinition: BodyDefinition, spawnOptions: KreepSpawnOptions? = null): Boolean {
    if (room.energyAvailable < bodyDefinition.cost) return false

    val body = bodyDefinition.getBiggest(room.energyAvailable)
    val newName = "${bodyDefinition.name}_T${body.tier}_${Game.time}"

    val code = this.spawnCreep(body.body.toTypedArray(), newName, spawnOptions ?: KreepSpawnOptions(CreepState.REFILL))
    when (code) {
        OK -> {
            println("spawning $newName with body $body")
            return true
        }
        ERR_NOT_ENOUGH_ENERGY, ERR_BUSY -> return false // do nothing
        else -> throw IllegalArgumentException("error code $code when spawning $newName with body $body")
    }
}

object GlobalSpawnQueue {

    @Serializable
    private val queue: MutableList<SpawnInfo>
    val spawnQueue: List<SpawnInfo>
        get() = queue

    private var modified: Boolean = false

    init {
        // load from memory
        queue = Memory.globalSpawnQueue?.queue?.toMutableList() ?: ArrayList()
        println("spawnqueue initialized")
    }

    fun push(bodyDefinition: BodyDefinition, spawnOptions: KreepSpawnOptions? = null) {
        queue.add(SpawnInfo(bodyDefinition))
        modified = true
    }

    fun spawnCreeps(spawns: List<StructureSpawn>) {
        if (queue.isEmpty()) return

        for (spawn in spawns) {
            if (queue.isEmpty() || spawn.spawning != null) continue
            val (bodyDefinition) = queue.first()

            if (spawn.spawn(bodyDefinition, null)) {
                queue.removeAt(0)
                modified = true
            }
        }
    }

    /**
     * Save content of the queue to memory
     */
    fun save() {
        if (modified) Memory.globalSpawnQueue = CreepSpawnList(queue)
        modified = false
    }


    private var Memory.globalSpawnQueue: CreepSpawnList?
        get() {
            val internal = this.asDynamic().globalSpawnQueue
            return if (internal == null) null else JSON.parse(internal)
        }
        set(value) {
            val stringyfied = if (value == null) null else JSON.stringify(value)
            println("stringyfied=$stringyfied")

            this.asDynamic().globalSpawnQueue = stringyfied
        }
}

fun requestCreep(bodyDefinition: BodyDefinition, spawnOptions: KreepSpawnOptions? = null) {
    GlobalSpawnQueue.push(bodyDefinition, spawnOptions)
}

@Serializable
data class SpawnInfo(val bodyDefinition: BodyDefinition)

@Serializable
data class CreepSpawnList(val queue: List<SpawnInfo>)