package ch.mikewong.adventofcode.common.extensions

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * @return True if there is an overlap between [this] and the [other] IntRange
 */
fun IntRange.overlapsWith(other: IntRange, endInclusive: Boolean = true): Boolean {
	val overlap = min(last, other.last) - max(first, other.first)
	return if (endInclusive) overlap >= 0 else overlap > 0
}

/**
 * @return True if there is an overlap between [this] and the [other] IntRange
 */
fun LongRange.overlapsWith(other: LongRange, endInclusive: Boolean = true): Boolean {
	val overlap = min(last, other.last) - max(first, other.first)
	return if (endInclusive) overlap >= 0 else overlap > 0
}


/**
 * @return True if either [this] or the [other] IntRange is fully contained within the other one
 */
fun IntRange.fullyOverlapsWith(other: IntRange): Boolean {
	return (first >= other.first && last <= other.last) || (other.first >= first && other.last <= last)
}

/**
 * Merge two overlapping ranges
 */
fun IntRange.merge(other: IntRange): IntRange {
	return min(this.first, other.first) .. max(this.last, other.last)
}

/**
 * Merge two overlapping ranges
 */
fun LongRange.merge(other: LongRange): LongRange {
	return min(this.first, other.first) .. max(this.last, other.last)
}

/**
 * @return The size of this range, meaning the number of items inside this range
 */
fun LongRange.size() = abs(last - first) + 1