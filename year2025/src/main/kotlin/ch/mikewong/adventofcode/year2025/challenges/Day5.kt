package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.mergeToUniqueRanges
import ch.mikewong.adventofcode.common.extensions.size

class Day5 : Day<Int, Long>(2025, 5, "Cafeteria") {

	private val ingredientIdRanges by lazy { parseRanges() }
	private val ingredientIds by lazy { inputGroups.last().map { it.toLong() } }

	/** 674 */
	override fun partOne(): Int {
		return ingredientIds.count { id ->
			ingredientIdRanges.any { range -> id in range }
		}
	}

	/** 352509891817881 */
	override fun partTwo(): Long {
		val uniqueRanges = ingredientIdRanges.mergeToUniqueRanges()
		return uniqueRanges.sumOf { it.size() }
	}

	private fun parseRanges(): List<LongRange> {
		return inputGroups.first().map { line ->
			val (start, end) = line.split("-").map { it.toLong() }
			start..end
		}
	}

}