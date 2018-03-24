package types

external interface Creep : RoomObject {
    val carry: dynamic
    val memory: dynamic
    val carryCapacity: Number
    val fatigue: Number
    val hits: Number
    val hitsMax: Number
    val id: String
    val my: Boolean
    val name: String
    val owner: Owner
    val spawning: Boolean
    val saying: String
    val ticksToLive: Number?

    fun attack(target: Creep): dynamic
    fun harvest(target: dynamic): Number
    fun moveTo(target: RoomPosition)
    fun moveTo(target: RoomPosition, opts: dynamic)
    fun transfer(target: Creep, resourceType: String, amount: Number = definedExternally): Number
    fun transfer(target: Structure, resourceType: String, amount: Number = definedExternally): Number
    fun upgradeController(target: StructureController): Number

}