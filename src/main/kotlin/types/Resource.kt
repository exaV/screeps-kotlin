package types

external class Resource : RoomObject {
    val amount: Int
    val resourceType: ResourceConstant
    override val id: String = definedExternally
    override val room: Room = definedExternally
    override val pos: RoomPosition = definedExternally
}