package screeps.game.one.building

import screeps.game.one.Context
import types.*

val StructureController.availableStorage
    get() = when {
        level >= 4 -> 1
        else -> 0
    }

val StructureController.availableTowers
    get() = when (level) {
        3, 4 -> 1
        5, 6 -> 2
        7 -> 3
        8 -> 6
        else -> 0
    }

val StructureController.availableExtensions
    get() = when (level) {
        1 -> 0
        2 -> 5
        3 -> 10
        4 -> 20
        5 -> 30
        6 -> 40
        7 -> 50
        8 -> 60
        else -> 0 //will not happen
    }


fun buildStorage(room: Room) {
    if (room.controller?.my != true) return //not our room
    if (room.controller?.availableStorage != 1) return //cannot build storage yet

    val hasStorage = room.storage != null
            || Context.constructionSites.values.any { it.structureType == STRUCTURE_STORAGE && it.room.name == room.name }
    if (hasStorage) return //already built or being  built

    val spawn = room.find<StructureSpawn>(FIND_MY_SPAWNS).first()

    var placed = false
    var pos = spawn.pos.copy(spawn.pos.x - 2)
    while (!placed) {
        val code = room.createConstructionSite(pos, STRUCTURE_STORAGE)
        when (code) {
            OK -> placed = true
            ERR_INVALID_TARGET -> pos = pos.copy(x = pos.x - 1)
            else -> println("unexpected return value $code when attempting to place storage")
        }
    }
}

fun buildTowers(room: Room) {
    if (room.controller?.my != true) return //not under control

    val numberOfTowers =
        Context.constructionSites.values.count { it.room.name == room.name && it.structureType == STRUCTURE_TOWER } + Context.myStuctures.values.count { it.room.name == room.name && it.structureType == STRUCTURE_TOWER }
    val towersToPlace = room.controller!!.availableTowers - numberOfTowers
    if (towersToPlace == 0) return //no need to place towers


    require(room.controller?.my == true)
    val spawn = room.find<StructureSpawn>(FIND_MY_SPAWNS).first()

    require(towersToPlace >= 0)
    var placed = 0

    var x = spawn.pos.x
    var y = spawn.pos.y + 1

    while (placed < towersToPlace) {
        y += 1
        val success = room.createConstructionSite(x, y, STRUCTURE_TOWER)
        when (success) {
            OK -> placed += 1
            ERR_INVALID_TARGET -> run { }
            else -> println("unexpected return value $success when attempting to place tower")
        }
    }

}

fun buildExtensions(room: Room) {
    require(room.controller?.my == true)

    val spawn = room.find<StructureSpawn>(FIND_MY_SPAWNS).first()

    val startPos = spawn.pos
    val numberOfExtensions: Int =
        room.find<Structure>(FIND_STRUCTURES).count { it.structureType == STRUCTURE_EXTENSION }
    val toPlace = room.controller!!.availableExtensions - numberOfExtensions
    var placed = 0

    val energySources = room.findEnergy()

    require(toPlace >= 0)
    val constructionSites = ArrayList<ConstructionSite>()
    while (placed < toPlace) {
        //find a road from spawn to energy source
        for (source in energySources) {

        }
    }
}
