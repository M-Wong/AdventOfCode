package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day19 : Day<Int, Long>(2024, 19, "Linen Layout") {

	private val towels by lazy {
		inputGroups.first().flatMap { line ->
			line.split(", ")
		}
	}

	private val patterns by lazy { inputGroups.last() }

	private val patternCache = mutableMapOf<String, Long>()

	// 228
	override fun partOne(): Int {
		return patterns.count { canPatternBeMade(it) }
	}

	// 584553405070389
	override fun partTwo(): Long {
		patternCache.clear()
		return patterns.sumOf { countPossibleArrangements(it) }
	}

	private fun canPatternBeMade(pattern: String): Boolean {
		if (pattern.isEmpty()) return true
		return towels.any { towel ->
			pattern.startsWith(towel) && canPatternBeMade(pattern.removePrefix(towel))
		}
	}

	private fun countPossibleArrangements(pattern: String): Long {
		return patternCache.getOrPut(pattern) {
			if (pattern.isEmpty()) {
				1
			} else {
				towels.sumOf { towel ->
					if (pattern.startsWith(towel)) countPossibleArrangements(pattern.removePrefix(towel)) else 0
				}
			}
		}
	}

}