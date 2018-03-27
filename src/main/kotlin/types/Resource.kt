package types

external interface Resource : RoomObject {
    val amount: Int
    val id: String
    val resourceType: String
}