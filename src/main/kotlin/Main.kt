import screeps.game.one.gameLoop

@Suppress("unused")
private object Traveler {
    init {
        js("var Traveler = require('Traveler');")
        println("global reset!")
        println("============================================================================================")
        println("imported traveler")
    }
}
/**
 * Entry point
 * is called by screeps
 *
 * must not be removed by DCE
 */
@Suppress("unused")
fun loop() {
    Traveler
    gameLoop()
}