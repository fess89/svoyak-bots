package net.egork.telegram.svoyak.scheduler

data class GameResultEntry(
        val name: String,
        val points: Int,
        val rating: Int,
        val delta: Int
) : Comparable<GameResultEntry> {
    override fun compareTo(o: GameResultEntry): Int {
        return o.points - points
    }
}
