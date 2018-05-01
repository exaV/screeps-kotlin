package types.base.prototypes

import types.base.global.MineralConstant

external class Mineral : RoomObject {
    val density: Int
    val mineralAmount: Int
    val mineralType: MineralConstant
    val ticksToRegeneration: Int
}