package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.util.asInts
import kotlin.math.abs

class Day7 : Day<Int, Int>(2021, 7, "The Treachery of Whales") {

	private val crabPositions = inputLines.first().split(",").asInts()
	private val minPosition = crabPositions.minOrNull() ?: 0
	private val maxPosition = crabPositions.maxOrNull() ?: 0

	override fun partOne() = calculateMinFuelNeeded { it }

	override fun partTwo() = calculateMinFuelNeeded { it * (it + 1) / 2 }

	/**
	 * @param fuelCost A lambda that calculates the fuel required to move a certain distance
	 */
	private fun calculateMinFuelNeeded(fuelCost: (Int) -> Int): Int {
		return (minPosition..maxPosition).minOf { from ->
			crabPositions.sumOf { to ->
				val distance = abs(from - to)
				fuelCost(distance)
			}
		}
	}

}