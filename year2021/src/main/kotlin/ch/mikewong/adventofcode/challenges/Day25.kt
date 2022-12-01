package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point
import ch.mikewong.adventofcode.common.util.toGridNotNull

class Day25 : Day<Int, Int>(2021, 25, "Sea Cucumber") {

	private val initialSeabed by lazy {
		inputLines.toGridNotNull { c ->
			when (c) {
				'v' -> Direction.SOUTH
				'>' -> Direction.EAST
				else -> null
			}
		}
	}

	private val xRange = inputLines.indices
	private val yRange = inputLines[0].indices

	override fun partOne(): Int {
		val seabed = initialSeabed.toMutableMap()
		var stepCount = 0

		// Move the sea cucumbers while there are any possible moves
		var canMove = true
		while (canMove) {
			val didMoveEast = moveInDirection(seabed, Direction.EAST)
			val didMoveSouth = moveInDirection(seabed, Direction.SOUTH)
			canMove = didMoveEast || didMoveSouth

			stepCount++
		}

		return stepCount
	}

	private fun moveInDirection(seabed: MutableMap<Point, Direction>, direction: Direction): Boolean {
		val moves = seabed.filterValues { it == direction }.mapNotNull { (from, direction) ->
			val to = from.move(direction).wrapAround(xRange, yRange)

			if (!seabed.containsKey(to)) {
				from to to
			} else {
				null
			}
		}

		moves.forEach { (from, to) ->
			seabed[to] = direction
			seabed.remove(from)
		}

		return moves.isNotEmpty()
	}

	override fun partTwo(): Int {
		return 0
	}

}