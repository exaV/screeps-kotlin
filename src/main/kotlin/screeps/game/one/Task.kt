package screeps.game.one

abstract class Task {
    abstract val priorityThreshold: Int
    abstract val priority: Int
    open val minCreeps = 0
}

class RefillTask : Task() {

    override val priorityThreshold: Int
        get() = 0
    override val priority: Int
        get() = 0
    override val minCreeps = 1

}