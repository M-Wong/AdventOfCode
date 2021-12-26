package ch.mikewong.adventofcode.util

import ch.mikewong.adventofcode.models.Point
import java.util.Collections.swap

/**
 * Convert a list of strings to a list of ints with the given [radix]
 */
fun List<String>.asInts(radix: Int = 10) = this.map { it.toInt(radix) }

/**
 * Convert a list of strings to a list of longs with the given [radix]
 */
fun List<String>.asLongs(radix: Int = 10) = this.map { it.toLong(radix) }

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
 * Converts a list of strings to a grid where each character is mapped to a point with x/y (row/column) and its mapping value.
 * Skips characters that map to null
 */
fun <T> List<String>.toGridNotNull(mapping: (Char) -> T?): Map<Point, T> {
	return mapIndexed { row, line ->
		line.mapIndexedNotNull { column, char ->
			mapping.invoke(char)?.let { Point(row, column) to it }
		}
	}.flatten().toMap()
}

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
 * Converts a list of strings to an char grid, where each character is mapped to a point with x/y (row/column) and its char value
 */
fun List<String>.toCharGrid(): Map<Point, Char> {
	return mapIndexed { row, line ->
		line.toCharArray().mapIndexed { column, char ->
			Point(row, column) to char
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

/**
 * Return a list with the element at [index] replaced with [newValue]
 */
fun <T> List<T>.set(index: Int, newValue: T): List<T> {
	val newList = this.toMutableList()
	newList[index] = newValue
	return newList
}

/**
 * Return a list with the element [oldValue] replaced with [newValue] or appended if it didn't exist
 */
fun <T> List<T>.set(oldValue: T, newValue: T): List<T> {
	val newList = this.toMutableList()
	val oldIndex = newList.indexOf(oldValue)
	if (oldIndex != -1) {
		newList[oldIndex] = newValue
	} else {
		newList.add(newValue)
	}
	return newList
}

/**
 * Return a list of all possible permutations given the elements in [this] collection
 */
fun <T> List<T>.permutations(): List<List<T>> {
	val result = mutableListOf<List<T>>()

	fun generate(k: Int, list: List<T>) {
		if (k == 1) {
			result.add(list.toList())
		} else {
			(0 until k).forEach { i ->
				generate(k - 1, list)
				if (k % 2 == 0) {
					swap(list, i, k - 1)
				} else {
					swap(list, 0, k - 1)
				}
			}
		}
	}

	generate(this.size, this.toList())
	return result
}

/**
 * Cartesian product for CharRanges.
 * The input is the range itself repeated n times
 *
 * @param n How often the Range is repeated
 * @return The cartesian prodcut of the range
 */
fun IntProgression.cartProd(n: Int): Sequence<List<Int>> {
	val ranges = repeat(n).toList().toTypedArray()
	return cartProd(*(ranges))
}

/**
 * Make a [Sequence] that returns object over and over again.
 * Runs indefinitely unless the [times] argument is specified.
 *
 * @param times How often the object is repeated. null means its repeated indefinitely
 */
fun <T : Any> T.repeat(times: Int? = null): Sequence<T> = sequence {
	var count = 0
	while (times == null || count++ < times) yield(this@repeat)
}

/**
 * Cartesian product of input [Iterable]. (https://en.wikipedia.org/wiki/Cartesian_product)
 * Roughly equivalent to nested for-loops in a generator expression.
 * For example, product(A, B) returns the same as ((x,y) for x in A for y in B).
 * The nested loops cycle like an odometer with the rightmost element advancing on every iteration.
 * This pattern creates a lexicographic ordering so that if the input’s iterables are sorted,
 * the product tuples are emitted in sorted order.
 *
 * @param items The input for which the cartesian product is calucalted
 *
 * @return A [Sequence] of [List] which contains the cartesian product
 *
 */
fun <T : Any> cartProd(vararg items: Iterable<T>): Sequence<List<T>> = sequence {
	if (items.all { it.iterator().hasNext() }) {
		val itemsIter = items.map { it.iterator() }.filter { it.hasNext() }.toMutableList()
		val currElement: MutableList<T> = itemsIter.map { it.next() }.toMutableList()
		loop@ while (true) {
			yield(currElement.toList())
			for (pos in itemsIter.count() - 1 downTo 0) {
				if (!itemsIter[pos].hasNext()) {
					if (pos == 0) break@loop
					itemsIter[pos] = items[pos].iterator()
					currElement[pos] = itemsIter[pos].next()
				} else {
					currElement[pos] = itemsIter[pos].next()
					break
				}
			}
		}
	}
}