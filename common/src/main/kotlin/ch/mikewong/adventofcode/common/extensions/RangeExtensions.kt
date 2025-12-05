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

/**
 * Merges all the ranges in [this] list into unique ranges by combining overlapping ranges. The resulting list contains only non-overlapping ranges.
 */
fun List<LongRange>.mergeToUniqueRanges(): List<LongRange> {
	val ranges = this.toMutableList()
	val uniqueRanges = mutableListOf<LongRange>()

	// Use ranges as a stack and keep processing while it is not empty
	while (ranges.isNotEmpty()) {
		// Use the first range as a reference
		val range = ranges.removeFirst()

		// Find all remaining ranges that overlap with the reference range
		val overlappingRanges = ranges.filter { it.overlapsWith(range) }
		ranges.removeAll(overlappingRanges)

		if (overlappingRanges.isEmpty()) {
			// If no ranges overlap with the reference, it is unique
			uniqueRanges.add(range)
		} else {
			// Fold the overlapping ranges onto the reference range by merging them and re-add the resulting range onto the stack
			val newRange = overlappingRanges.fold(range) { a, b -> a.merge(b) }
			ranges.add(newRange)
		}
	}

	return uniqueRanges
}