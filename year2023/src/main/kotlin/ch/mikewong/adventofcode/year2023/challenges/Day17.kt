package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.algorithms.dijkstra
import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toIntGrid
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day17 : Day<Int, Int>(2023, 17, "Clumsy Crucible") {

	private val heatLossMap = inputLines.toIntGrid()
	private val bounds = inputSize.toArea()

	override fun partOne(): Int {
		// Normal crucibles can move anywhere between 1 and 3 steps in a straight line
		return findPathWithMinimumHeatLoss(
			minimumStraightSteps = 0,
			maximumStraightSteps = 3,
		)
	}

	override fun partTwo(): Int {
		// Ultra crucibles can only move between 4 and 10 steps in a straight line
		return findPathWithMinimumHeatLoss(
			minimumStraightSteps = 4,
			maximumStraightSteps = 10,
		)
	}

	private fun findPathWithMinimumHeatLoss(
		minimumStraightSteps: Int,
		maximumStraightSteps: Int,
	): Int {
		val start = Point(0, 0)

		return dijkstra(
			startingNode = CruciblePath(start, 0, null),
			isTargetNode = {
				// The end is reached if the position is the bottom right and the crucible has done its minimum number of steps since the last turn
				it.point == bounds.bottomRight && it.stepsSinceLastTurn > minimumStraightSteps
			},
			neighbours = { state ->
				// Get all possible directions the crucible can move next
				val possibleDirections = when {
					// The very first step can move in any direction
					state.lastDirection == null -> Direction.lateral()

					// The crucible has reached its maximum number of allowed steps in a straight line and must make a 90° turn now
					state.stepsSinceLastTurn == maximumStraightSteps -> {
						if (state.lastDirection.isVertical()) {
							listOf(Direction.EAST, Direction.WEST)
						} else {
							listOf(Direction.NORTH, Direction.SOUTH)
						}
					}

					// The crucible has not yet moved its minimum number of steps in a straight line and must continue in the last direction
					state.stepsSinceLastTurn < minimumStraightSteps -> listOf(state.lastDirection)

					// The crucible can move in any direction except backwards
					else -> Direction.lateral().minus(state.lastDirection.opposite())
				}.filter { bounds.contains(state.point.move(it)) }

				// Map the possible directions to new crucible paths
				possibleDirections.map { direction ->
					CruciblePath(
						point = state.point.move(direction),
						stepsSinceLastTurn = if (direction == state.lastDirection) state.stepsSinceLastTurn + 1 else 1,
						lastDirection = direction,
					)
				}
			},
			costFunction = { _, next ->
				// The cost function for the crucible is the heat loss value of the position it moves to
				heatLossMap.getValue(next.point)
			}
		).totalCost
	}

	private data class CruciblePath(
		val point: Point,
		val stepsSinceLastTurn: Int,
		val lastDirection: Direction?,
	)

}