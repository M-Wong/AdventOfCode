package ch.mikewong.adventofcode.util

import ch.mikewong.adventofcode.models.Point

/**
 * Convert a list of strings to a list of ints with the given [radix]
 */
fun List<String>.asInts(radix: Int = 10) = this.map { it.toInt(radix) }

/**
 * Shift the contents of an array to the left by [count] positions and fill the top part with the value of [defaultProvider]
 */
fun <T> Array<T>.shiftLeft(count: Int = 1, defaultProvider: (Int) -> T) {
	indices.forEach { idx ->
		if (idx < size - count) {
			this[idx] = this[idx + count]
		} else {
			this[idx] = defaultProvider.invoke(idx)
		}
	}
}

/**
 * Check if [this] contains the exact same characters as [other] (but maybe in different orders)
 */
fun String.hasSameCharsAs(other: String) = this.toSortedSet() == other.toSortedSet()

/**
 * Check if [this] contains all the characters of [other] (but maybe more)
 */
fun String.containsAllCharsOf(other: String) = other.all { this.contains(it) }

/**
 * Converts a list of strings to an int grid, where each character is mapped to a point with x/y (row/column) and its integer value
 */
fun List<String>.toIntGrid(): Map<Point, Int> {
	return mapIndexed { row, line ->
		line.toCharArray().mapIndexed { column, char ->
			Point(row, column) to char.digitToInt()
		}
	}.flatten().toMap()
}

/**
 * Return the [count] highest numbers in this list
 */
fun <T: Number> List<T>.top(count: Int): List<T> {
	return sortedByDescending { it.toDouble() }.take(count)
}