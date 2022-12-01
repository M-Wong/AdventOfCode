package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.util.containsAllCharsOf
import ch.mikewong.adventofcode.common.util.hasSameCharsAs

class Day8 : Day<Int, Int>(2021, 8, "Seven Segment Search") {

	private val digitMappings = arrayOf(
		"abcefg", 	// 0
		"cf",		// 1
		"ecdeg",	// 2
		"acdfg",	// 3
		"bcdf",		// 4
		"abdfg",	// 5
		"abdefg",	// 6
		"acf",		// 7
		"abcdefg",	// 8
		"abcdfg",	// 9
	)
	private val uniqueLengthDigits = digitMappings.groupBy { it.length }.filter { it.value.size == 1 }

	private val signalPatterns = inputLines.map {
		val parts = it.split(" | ")
		Process(parts.first().split(" "), parts.last().split(" "))
	}

	override fun partOne(): Int {
		val uniqueLengths = uniqueLengthDigits.map { it.key }
		return signalPatterns.sumOf { signals ->
			signals.observations.count { pattern ->
				pattern.length in uniqueLengths
			}
		}
	}

	override fun partTwo(): Int {
		return signalPatterns.sumOf { signals ->
			val resolvedPatterns = Array(10) { "" }

			// Group the signal patterns by their length and add them to an array where the index equals the length
			val patternsByLength = Array(8) { idx -> signals.patterns.filter { it.length == idx } }

			// Resolve the patterns with unique lengths
			resolvedPatterns[1] = patternsByLength[2].single()
			resolvedPatterns[4] = patternsByLength[4].single()
			resolvedPatterns[7] = patternsByLength[3].single()
			resolvedPatterns[8] = patternsByLength[7].single()

			// Pattern 9 is the only pattern that contains all the same characters as pattern 4
			resolvedPatterns[9] = signals.patterns.first { it.length == 6 && it.containsAllCharsOf(resolvedPatterns[4]) }

			// Pattern 6 is the only 6-length pattern that does not contain all the same characters as pattern 1
			resolvedPatterns[6] = signals.patterns.first { it.length == 6 && !it.containsAllCharsOf(resolvedPatterns[1]) }

			// Pattern 0 is the olny 6-length pattern left that was not yet resolved
			resolvedPatterns[0] = signals.patterns.first { it.length == 6 && !resolvedPatterns.contains(it) }

			// Pattern 3 is the only 5-length pattern that contains all the same characters as pattern 1
			resolvedPatterns[3] = signals.patterns.first { it.length == 5 && it.containsAllCharsOf(resolvedPatterns[1]) }

			// Pattern 5 is the only 5-length pattern that, combined with the characters for pattern 1, contains all the same characters as pattern 9
			resolvedPatterns[5] = signals.patterns.first { it.length == 5 && (it + resolvedPatterns[1]).hasSameCharsAs(resolvedPatterns[9]) }

			// Pattern 2 is the last unresolved pattern
			resolvedPatterns[2] = signals.patterns.first { !resolvedPatterns.contains(it) }

			signals.observations.map { pattern ->
				resolvedPatterns.indexOfFirst { pattern.hasSameCharsAs(it) }
			}.joinToString("").toInt()
		}
	}

	private data class Process(val patterns: List<String>, val observations: List<String>)
}