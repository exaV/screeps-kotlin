@file:Suppress("UNCHECKED_CAST")

package types

import kotlin.js.Json

fun <T> jsonToMap(json: Json): Map<String, T> {
    val map: MutableMap<String, T> = linkedMapOf()
    for (key in js("Object").keys(json)) {
        map.put(key, json[key] as T)
    }
    return map
}

class VisualizePath(val visualizePathStyle: Style = Style("#ffaa00")) {
    constructor(stroke: String) : this(Style(stroke))
}

class Style(val stroke: String)