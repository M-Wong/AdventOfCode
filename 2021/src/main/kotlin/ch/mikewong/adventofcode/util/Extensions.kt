package ch.mikewong.adventofcode.util

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

fun String.containsNumberOfCharacters(other: String, count: Int = 1) = other.count { this.contains(it) } == count