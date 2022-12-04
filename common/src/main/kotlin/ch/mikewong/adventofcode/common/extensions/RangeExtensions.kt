package ch.mikewong.adventofcode.common.extensions

import kotlin.math.min
import kotlin.math.max

/**
 * Returns true if there is an overlap between [this] and the [other] IntRange
 */
fun IntRange.overlapsWith(other: IntRange, endInclusive: Boolean = true): Boolean {
	val overlap = min(last, other.last) - max(first, other.first)
	return if (endInclusive) overlap >= 0 else overlap > 0
}

/**
 * @return True if either [this] or the [other] IntRange is fully contained within the other one
 */
fun IntRange.fullyOverlapsWith(other: IntRange): Boolean {
	return (first >= other.first && last <= other.last) || (other.first >= first && other.last <= last)
}