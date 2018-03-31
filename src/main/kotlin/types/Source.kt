package types


external class Source : RoomObject, GameObject {
    val energy: Number
    val energyCapacity: Number
    val ticksToRegeneration: Number

    override val pos: RoomPosition
        get() = definedExternally
    override val room: Room
        get() = definedExternally
    override val id: String
        get() = definedExternally
}