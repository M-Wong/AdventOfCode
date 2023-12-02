package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.product
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Point

class Day3 : Day<Int, Int>(2020, 3, "Toboggan Trajectory") {

	private val treePositions by lazy { readInput() }
	private val startPosition = Point(0, 0)

	override fun partOne(): Int {
		val slope = Slope(right = 3, down = 1)
		return countTreesOnSlope(slope)
	}

	override fun partTwo(): Int {
		val slopes = listOf(
			Slope(right = 1, down = 1), // 60
			Slope(right = 3, down = 1), // 191
			Slope(right = 5, down = 1), // 64
			Slope(right = 7, down = 1), // 63
			Slope(right = 1, down = 2), // 32
		)
		return slopes.map { countTreesOnSlope(it) }.product()
	}

	private fun readInput() = inputLines.toGridNotNull { char -> char.takeIf { it == '#' } }.keys

	private fun countTreesOnSlope(slope: Slope): Int {
		val requiredIterations = inputSize.height / slope.down

		var currentPosition = startPosition
		return (1 until requiredIterations).count { i ->
			currentPosition = currentPosition.move(slope.down, slope.right)
				.wrapAround(inputSize.rowRange(), inputSize.colRange())
			treePositions.contains(currentPosition)
		}
	}

	private data class Slope(val right: Int, val down: Int)

}