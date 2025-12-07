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
 * Returns [this] string with all non-digits stripped away. E.g. "a1b2c3 d4 e5" -> "12345"
 */
fun String.allDigits() = filter { it.isDigit() }

/**
 * Returns [this] string as a list of integers. E.g. "a1 2b3 45 6 test    7" -> [1, 2, 3, 45, 6, 7]
 */
fun String.allInts() = "-?\\d+".toRegex().findAll(this).map { it.value.toInt() }.toList()

/**
 * Returns [this] string as a list of integers. E.g. "a1 2b3 45 6 test    7" -> [1, 2, 3, 45, 6, 7]
 */
fun String.allLongs() = "-?\\d+".toRegex().findAll(this).map { it.value.toLong() }.toList()

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

/**
 * Returns the number of different characters in two equal-lenght strings
 */
fun String.differentCharacters(other: String): Int {
	if (this.length != other.length) {
		throw IllegalArgumentException("Input strings must have the same length")
	}

	return this.indices.count { i ->
		this[i] != other[i]
	}
}

/**
 * Returns a pair of strings, where [Pair.first] is the first half of the string and [Pair.second] is the second half
 * If the string has an odd length, the second half will be one character longer than the first half
 */
fun String.splitIntoTwo(): Pair<String, String> {
	val middle = length / 2
	return substring(0, middle) to substring(middle)
}

/**
 * Splits [this] string into substrings divided by the given [indices]
 */
fun String.splitBy(indices: List<Int>): List<String> {
	return buildList {
		var currentIdx = 0
		indices.forEach { idx ->
			if (idx > currentIdx) {
				add(substring(currentIdx, idx))
			}
			currentIdx = idx
		}

		if (currentIdx < lastIndex) {
			add(substring(currentIdx))
		}
	}
}