package types.base.global

import types.base.CPU
import types.base.GlobalControlLevel
import types.base.Shard
import types.base.prototypes.GameObject
import kotlin.js.Json

external object Game {
    val constructionSites: Json
    val cpu: CPU
    val creeps: Json
    val flags: Json
    val gcl: GlobalControlLevel
    val map: GameMap
    val market: Market
    val resources: Json
    val rooms: Json
    val shard: Shard
    val spawns: Json
    val structures: Json
    val time: Number
    fun <T : GameObject> getObjectById(id: String?): T?
    fun notify(message: String, groupInterval: Number? = definedExternally /* null */): Nothing?
}