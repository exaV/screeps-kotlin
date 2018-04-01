package types

external class Creep : RoomObject {
    val carry: Storage
    val memory: CreepMemory
    val carryCapacity: Int
    val fatigue: Number
    val hits: Int
    val hitsMax: Int
    val my: Boolean
    val name: String
    val owner: Owner
    val spawning: Boolean
    val saying: String
    val ticksToLive: Number?
    val body: Array<BodyPart>

    override val pos: RoomPosition
    override val room: Room
    override val id: String

    fun attack(target: Creep): dynamic
    fun harvest(target: Source): Number
    // fun harvest(target: Mineral): Number
    fun moveTo(target: RoomPosition, opts: MoveToOpts? = definedExternally): Number

    fun moveTo(x: Int, y: Int, opts: MoveToOpts? = definedExternally): Number
    fun moveByPath(path: Array<PathStep>): Number
    /**
     * must be serialized path string
     */
    fun moveByPath(path: String): Number
    fun transfer(target: Creep, resourceType: String, amount: Number = definedExternally): Number
    fun transfer(target: Structure, resourceType: String, amount: Number = definedExternally): Number
    fun upgradeController(target: StructureController): Number
    fun say(message: String, toPublic: Boolean? = definedExternally): Number
    fun build(target: ConstructionSite): Number
    fun pickup(target: Resource): Number
    fun repair(structure: Structure): Number
    fun withdraw(structureContainer: Structure, resourceType: String, amount: Number = definedExternally): Number
}

external interface Storage {
    val energy: Int
    val power: Int?
}

external interface BodyPart {
    val boost: String?
    val type: BodyType
    val hits: Int
}

class MoveToOpts(val reusePath: Int = 5,
                 val serializeMemory: Boolean = true,
                 val noPathFinding: Boolean = false,
                 val visualizePathStyle: Style = Style(stroke = "#ffaa00"),
                 ignoreCreeps: Boolean = true) : FindPathOpts(ignoreCreeps = ignoreCreeps)
