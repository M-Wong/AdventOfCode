package ch.mikewong.adventofcode.common.extensions

/**
 * Finds the first index of an element that matches the [predicate], starting from the [start] index
 *
 * @return An index within [indices] or -1 if no element matches the [predicate]
 */
fun <T> Array<T>.indexOfFirst(start: Int = 0, predicate: (T) -> Boolean): Int {
	for (i in start until size) {
		if (predicate(this[i])) return i
	}
	return -1
}

/**
 * Finds the last index of an element that matches the [predicate], starting from the [start] index
 *
 * @return An index within [indices] or -1 if no element matches the [predicate]
 */
fun <T> Array<T>.indexOfLast(start: Int = lastIndex, predicate: (T) -> Boolean): Int {
	for (i in start downTo 0) {
		if (predicate(this[i])) return i
	}
	return -1
}