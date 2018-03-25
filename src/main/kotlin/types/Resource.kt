package types

external interface Resource : RoomObject {
    val amount: Number
    val id: String
    val resourceType: String
}