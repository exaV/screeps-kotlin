package types


external interface Source : RoomObject, GameObjectId {
    val energy: Number
    val energyCapacity: Number
    val ticksToRegeneration: Number
}