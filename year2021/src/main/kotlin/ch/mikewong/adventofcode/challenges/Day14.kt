package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.util.increment
import ch.mikewong.adventofcode.util.longCount
import ch.mikewong.adventofcode.util.range
import ch.mikewong.adventofcode.util.toPair

class Day14 : Day<Long, Long>(14, "Extended Polymerization") {

	private val polymerTemplate = inputLines.first()
	private val pairInsertionRules = inputGroups.last().associate { it.split(" -> ").toPair() }

	override fun partOne() = countPolymers(10).range()

	override fun partTwo() = countPolymers(40).range()

	private fun countPolymers(stepCount: Int): Map<Char, Long> {
		val initialPairCount = polymerTemplate.windowed(2).longCount { it }
		val finalPairCount = insertPolymers(stepCount, initialPairCount).toMutableMap()
		return finalPairCount
			.map { (pair, count) -> pair.first() to count } // Map the pair count to a count of each individual polymer
			.groupBy { it.first } // ... group them
			.mapValues { (_, count) -> count.sumOf { it.second } } // ... then sum the individual counts
			.toMutableMap()
			.increment(polymerTemplate.last()) // ... and increment the count of the last character in the polymer template
	}

	private fun insertPolymers(stepCount: Int, previousPairCount: Map<String, Long>): Map<String, Long> {
		val currentPairCount = mutableMapOf<String, Long>()

		previousPairCount.forEach { (pair, count) ->
			// For each pair count in the previous iteration, find an insertion rule and increment the count of the two pairs that make up the original pair and the new inserted polymer
			pairInsertionRules[pair]?.let {
				currentPairCount.increment(pair.first() + it, count).increment(it + pair.last(), count)
			}
		}

		return if (stepCount > 1) {
			insertPolymers(stepCount - 1, currentPairCount)
		} else {
			currentPairCount
		}
	}
}