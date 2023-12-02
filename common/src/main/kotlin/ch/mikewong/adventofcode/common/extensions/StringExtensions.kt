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
 * Return a substring until the first occurrence of [delimiter]. If not found, will return the whole string
 */
fun String.substringUntil(delimiter: String): String {
	val index = indexOf(delimiter)
	return this.takeIf { index == -1 } ?: substring(0, index)
}

/**
 * Returns true if all characters in this string are unique, false if any character appears more than once
 */
fun String.allUnique() = all(hashSetOf<Char>()::add)

/**
 * Returns the first digit of a string as an integer or null if no digit occurs in [this]
 */
fun String.firstDigit() = firstOrNull { it.isDigit() }?.digitToInt()

/**
 * Returns the last digit of a string as an integer or null if no digit occurs in [this]
 */
fun String.lastDigit() = lastOrNull { it.isDigit() }?.digitToInt()

/**
 * Returns the first indext of [string] within [this], or [default] if not found
 */
fun String.indexOfOrElse(string: String, default: Int = Int.MAX_VALUE) = indexOf(string).takeIf { it != -1 } ?: default

/**
 * Returns the last index of [string] within [this], or [default] if not found
 */
fun String.lastIndexOfOrElse(string: String, default: Int = Int.MIN_VALUE) = lastIndexOf(string).takeIf { it != -1 } ?: default