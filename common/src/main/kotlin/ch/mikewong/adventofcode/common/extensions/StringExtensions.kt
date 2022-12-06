package ch.mikewong.adventofcode.common.extensions

/**
 * Check if [this] contains the exact same characters as [other] (but maybe in different orders)
 */
fun String.hasSameCharsAs(other: String) = this.toSortedSet() == other.toSortedSet()

/**
 * Check if [this] contains all the characters of [other] (but maybe more)
 */
fun String.containsAllCharsOf(other: String) = other.all { this.contains(it) }

/**
 * Check if a string only consists of uppercase or whitespace characters
 */
fun String.isAllUpperCase() = this.all { it.isWhitespace() || it.isUpperCase() }

/**
 * Check if a string only consists of lowercase or whitespace characters
 */
fun String.isAllLowerCase() = this.all { it.isWhitespace() || it.isLowerCase() }

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
 * Returns true if all characters in this string are unique, false if any character appears more than once
 */
fun String.allUnique() = all(hashSetOf<Char>()::add)