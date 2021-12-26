package ch.mikewong.adventofcode.util

import ch.mikewong.adventofcode.models.Point
import java.util.*

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
 * Create a pair out of a list using the first and last element (other elements, assuming there are any, are discarded)
 */
fun List<String>.toPair() = first() to last()

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
 * Count the number of instances
 */
fun <T, K> List<T>.longCount(selector: (T) -> K) = groupingBy(selector).eachCount().mapValues { it.value.toLong() }

/**
 * Returns the product of a list of longs
 */
fun List<Long>.product(): Long {
	return reduce { acc, number -> acc * number }
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
					Collections.swap(list, i, k - 1)
				} else {
					Collections.swap(list, 0, k - 1)
				}
			}
		}
	}

	generate(this.size, this.toList())
	return result
}