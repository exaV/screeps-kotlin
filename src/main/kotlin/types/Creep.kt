package types

external class Creep : RoomObject {
    val carry: Carry
    val memory: CreepMemory
    val carryCapacity: Int
    val fatigue: Number
    val hits: Number
    val hitsMax: Number
    val my: Boolean
    val name: String
    val owner: Owner
    val spawning: Boolean
    val saying: String
    val ticksToLive: Number?

    override val pos: RoomPosition
    override val room: Room
    override val id: String

    fun attack(target: Creep): dynamic
    fun harvest(target: Source): Number
    // fun harvest(target: Mineral): Number
    fun moveTo(target: RoomPosition)

    fun moveTo(target: RoomPosition, opts: dynamic): Int
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
    fun repair(structure: OwnedStructure): Number
}

external interface Carry {
    val energy: Int
    val power: Int?
}