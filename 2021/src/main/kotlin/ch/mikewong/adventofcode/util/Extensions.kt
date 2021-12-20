package ch.mikewong.adventofcode.util

import ch.mikewong.adventofcode.models.Point
import kotlin.math.abs

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

/**
 * Return the middle element of a list assuming it has an odd number of entries
 */
fun <T> List<T>.middle(): T {
	if (size % 2 == 0) throw IllegalStateException("Can't get middle element from collection with even size")
	return this[size / 2]
}

/**
 * Check if a string only consists of uppercase or whitespace characters
 */
fun String.isAllUpperCase() = this.all { it.isWhitespace() || it.isUpperCase() }

/**
 * Check if a string only consists of lowercase or whitespace characters
 */
fun String.isAllLowerCase() = this.all { it.isWhitespace() || it.isLowerCase() }

/**
 * Create a pair out of a list using the first and last element (other elements, assuming there are any, are discarded)
 */
fun List<String>.toPair() = first() to last()

/**
 * Count the number of instances
 */
fun <T, K> List<T>.longCount(selector: (T) -> K) = groupingBy(selector).eachCount().mapValues { it.value.toLong() }

/**
 * Increment the value of [key] in a map with value type long by [amount]
 */
fun <T> MutableMap<T, Long>.increment(key: T, amount: Long = 1L): MutableMap<T, Long> {
	this[key] = this.getOrDefault(key, 0L) + amount
	return this
}

/**
 * Sum up the values of two maps with a long value type
 */
fun <T> Map<T, Long>.addTogether(other: Map<T, Long>): Map<T, Long> {
	val result = this.toMutableMap()
	other.forEach { (k, v) ->
		if (result.containsKey(k)) {
			result[k] = result[k]!! + v
		} else {
			result[k] = v
		}
	}
	return result
}

/**
 * Pair the keys of two maps that intersect by their value
 */
fun <K, V> Map<K, V>.keysIntersectingByValue(other: Map<K, V>): List<Pair<K, K>> {
	val result = mutableListOf<Pair<K, K>>()
	val intersection = this.values.intersect(other.values)
	this.filterValues { intersection.contains(it) }.forEach { (k, v) ->
		result += k to (other.filterValues { it == v }.keys.single())
	}
	return result
}

/**
 * Return the range of values in a map with value type long
 */
fun <T> Map<T, Long>.range(): Long {
	return maxOf { it.value } - minOf { it.value }
}

/**
 * Returns the product of a list of longs
 */
fun List<Long>.product(): Long {
	return reduce { acc, number -> acc * number }
}

/**
 * Return a substring between two delimiters or [defaultValue] if either of them is not found
 */
fun String.substringBetween(startDelimiter: String, endDelimiter: String, defaultValue: String = ""): String {
	val startIndex = indexOf(startDelimiter) + startDelimiter.length
	val endIndex = indexOf(endDelimiter, startIndex)
	return if (startIndex != -1 && endIndex != -1) {
		substring(startIndex, endIndex)
	} else {
		defaultValue
	}
}