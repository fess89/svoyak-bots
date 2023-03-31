package net.egork.telegram.svoyak.data

internal data class RatingEntry(val name: String, val rating: Int) : Comparable<RatingEntry> {
    override fun compareTo(o: RatingEntry): Int {
        return o.rating - rating
    }
}
