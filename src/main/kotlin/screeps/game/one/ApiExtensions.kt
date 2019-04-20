package screeps.game.one

import screeps.api.*
import screeps.api.structures.Structure
import screeps.api.structures.StructureSpawn

fun Room.findStructures() = find<Structure>(FIND_STRUCTURES)
fun Room.findMyConstructionSites() = find<ConstructionSite>(FIND_MY_CONSTRUCTION_SITES)
fun Room.findMySpawns() = find<StructureSpawn>(FIND_MY_SPAWNS)
fun Room.findEnergy() = find<Source>(FIND_SOURCES)
fun Room.findCreeps() = find<Creep>(FIND_CREEPS)
fun Room.findMyCreeps() = find<Creep>(FIND_MY_CREEPS)
fun Room.findConstructionSites() = find<ConstructionSite>(FIND_CONSTRUCTION_SITES)
fun Room.findDroppedEnergy() = find<Resource>(FIND_DROPPED_ENERGY)