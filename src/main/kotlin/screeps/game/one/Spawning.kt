package screeps.game.one

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import screeps.game.one.kreeps.BodyDefinition
import types.base.global.*
import types.base.prototypes.structures.SpawnOptions
import types.base.prototypes.structures.StructureSpawn

fun StructureSpawn.spawn(bodyDefinition: BodyDefinition, spawnOptions: KreepSpawnOptions? = null): Boolean {
    if (room.energyAvailable < bodyDefinition.cost) return false

    val body = bodyDefinition.getBiggest(room.energyAvailable)
    val newName = "${bodyDefinition.name}_T${body.tier}_${Game.time}"

    val actualSpawnOptions = spawnOptions ?: GlobalSpawnQueue.defaultSpawnOptions
    val code = this.spawnCreep(
        body.body.toTypedArray(),
        newName,
        actualSpawnOptions.toSpawnOptions()
    )
    return when (code) {
        OK -> {
            println("spawning $newName with body $body")
            true
        }
        ERR_NOT_ENOUGH_ENERGY, ERR_BUSY -> false // do nothing
        else -> throw IllegalArgumentException("error code $code when spawning $newName with body $body")
    }
}

object GlobalSpawnQueue {

    @Serializable
    private val queue: MutableList<SpawnInfo>
    val spawnQueue: List<SpawnInfo>
        get() = queue

    private var modified: Boolean = false
    val defaultSpawnOptions = KreepSpawnOptions(state = CreepState.IDLE)

    init {
        // load from memory
        queue = try {
            Memory.globalSpawnQueue?.queue?.toMutableList() ?: ArrayList()
        } catch (e: Error) {
            println("Error while initializing GlobalSpawnQueue: $e")
            ArrayList()
        }
        println("spawnqueue initialized to $queue")
    }

    fun push(bodyDefinition: BodyDefinition, spawnOptions: KreepSpawnOptions? = null) {
        queue.add(SpawnInfo(bodyDefinition, spawnOptions ?: defaultSpawnOptions))
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

fun requestCreep(bodyDefinition: BodyDefinition, spawnOptions: KreepSpawnOptions) {

    val candidate = Context.creeps.values.firstOrNull { it.body.contentEquals(bodyDefinition.bodyPartConstant) }
    if (candidate != null) {
        spawnOptions.transfer(candidate.memory)
    } else {
        GlobalSpawnQueue.push(bodyDefinition, spawnOptions)
    }

}

@Serializable
data class SpawnInfo(val bodyDefinition: BodyDefinition, val spawnOptions: KreepSpawnOptions)

@Serializable
data class CreepSpawnList(val queue: List<SpawnInfo>)

@Serializable
class KreepSpawnOptions(
    private val state: CreepState = CreepState.IDLE,
    private val missionId: String? = null,
    private val targetId: String? = null,
    private val assignedEnergySource: String? = null
) {
    fun toSpawnOptions(): SpawnOptions {
        return object : SpawnOptions {
            override val memory = object : CreepMemory {}.apply { transfer(this) }
        }
    }

    fun transfer(memory: CreepMemory) {
        memory.state = state
        memory.missionId = missionId
        memory.targetId = targetId
        memory.assignedEnergySource = assignedEnergySource
    }
}


