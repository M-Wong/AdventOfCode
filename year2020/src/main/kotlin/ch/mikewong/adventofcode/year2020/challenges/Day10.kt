package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asInts

class Day10 : Day<Int, Long>(2020, 10, "Adapter Array") {

	private val adapterJoltages = inputLines.asInts().sorted()
	private val builtInJoltage = adapterJoltages.max() + 3

	override fun partOne(): Int {
		val joltageDifferences = adapterJoltages.plus(builtInJoltage)
			.runningFold(Pair(0, 0)) { (previousAdapter, _), adapter ->
				// Create a running list of joltage differences
				adapter to (adapter - previousAdapter)
			}

		// Count the number of one and three joltage differences
		val oneJoltageDiffs = joltageDifferences.count { (_, diff) -> diff == 1 }
		val threeJoltageDiffs = joltageDifferences.count { (_, diff) -> diff == 3 }
		return oneJoltageDiffs * threeJoltageDiffs
	}

	override fun partTwo(): Long {
		return countPossibleCombinations(0, adapterJoltages)
	}

	/**
	 * A cache that stores the number of possible combinations for current state based on the current joltage and the list of remaining adapters
	 */
	private val combinationCache = mutableMapOf<Pair<Int, List<Int>>, Long>()

	private fun countPossibleCombinations(currentJoltage: Int, remainingAdapters: List<Int>): Long {
		val key = currentJoltage to remainingAdapters
		combinationCache[key]?.let { return it }

		if (remainingAdapters.isEmpty()) {
			// If there are no remaining adapters, check if the built-in joltage can be reached, if so, this is a valid combination
			return if (builtInJoltage - currentJoltage <= 3) 1 else 0
		}

		// Get a list of next possible adapters (with a joltage 1, 2 or 3 higher than the current one)
		val nextPossibleAdapters = remainingAdapters.takeWhile { it - currentJoltage <= 3 }

		// Count the number of possible combinations for each of these next possible adapters
		val possibilities = nextPossibleAdapters.map { adapter ->
			countPossibleCombinations(adapter, remainingAdapters.filter { it > adapter })
		}

		return possibilities.sum().also { combinationCache[key] = it }
	}

}