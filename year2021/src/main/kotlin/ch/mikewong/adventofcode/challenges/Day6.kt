package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.util.asInts
import ch.mikewong.adventofcode.util.shiftLeft

class Day6 : Day<Long, Long>(6, "Lanternfish") {

	companion object {
		private const val FISH_GROWTH_RATE = 7
		private const val FIRST_CYCLE_DELAY = 2
	}

	private val lanternFish = inputLines.first().split(",").asInts()

	override fun partOne() = getFishCountAfterDays(80)

	override fun partTwo() = getFishCountAfterDays(256)

	private fun getFishCountAfterDays(days: Int): Long {
		// Create an array for each possible growth cycle day a fish can be in
		val dailyFishCount = Array(FISH_GROWTH_RATE + FIRST_CYCLE_DELAY) { 0L }
		lanternFish.forEach { dailyFishCount[it]++ }

		repeat(days) {
			// Get the number of fish that are about to reproduce, then shift the array one position to the left and increase the number of newly spawned fish
			val fishOnZeroDays = dailyFishCount.first()
			dailyFishCount.shiftLeft { fishOnZeroDays }
			dailyFishCount[FISH_GROWTH_RATE - 1] += fishOnZeroDays
		}
		return dailyFishCount.sum()
	}

}