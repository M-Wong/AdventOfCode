package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.abs
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point
import kotlin.math.abs

class Day17 : Day<Long, Long>(2022, 17, "Pyroclastic Flow") {

	private val rockShapes = listOf(
		setOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3)),
		setOf(Point(0, 1), Point(1, 0), Point(1, 1), Point(1, 2), Point(2, 1)),
		setOf(Point(0, 2), Point(1, 2), Point(2, 0), Point(2, 1), Point(2, 2)),
		setOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0)),
		setOf(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1)),
	)
	private val pushDirections by lazy { input.map { it.toDirection() } }

	private val chamberWidth = 7
	private val rockSpawnLeftMargin = 2
	private val rockSpawnBottomMargin = 3

	override fun partOne(): Long {
		val finalState = simulateRockFall(2022)
		return finalState.height
	}

	override fun partTwo(): Long {
		val totalRockCount = 1000000000000L

		// Assuming there is at least one set of push directions before the regular cycle (aka the pre-cycle)
		val preCycleStates = mutableListOf(
			simulateRockFall(totalRockCount) { state -> state.isStartOfPushDirectionSet && state.fallenRockCount > 0 }
		)

		// Keep track of two subsequent sets of push directions to find when the rock fall cycle will repeat
		var firstPushDirectionState = simulateRockFall(totalRockCount) { state ->
			state.isStartOfPushDirectionSet && state.fallenRockCount > preCycleStates.first().fallenRockCount
		}
		var secondPushDirectionState = simulateRockFall(totalRockCount) { state ->
			state.isStartOfPushDirectionSet && state.fallenRockCount > firstPushDirectionState.fallenRockCount
		}

		// Keep track of the rock height that was added during each set of push directions and the corresponding state
		val rockHeightsPerState = mutableMapOf<Long, SimulationState>()
		while (!rockHeightsPerState.contains(secondPushDirectionState.height - firstPushDirectionState.height)) {
			// The cycle is found when the rock heights during each push direction start to repeat
			// This assumes that there are no push direction sets that produce duplicate rock heights within the cycle
			rockHeightsPerState[secondPushDirectionState.height - firstPushDirectionState.height] = secondPushDirectionState
			firstPushDirectionState = secondPushDirectionState
			secondPushDirectionState = simulateRockFall(totalRockCount) { state -> state.isStartOfPushDirectionSet && state.fallenRockCount > firstPushDirectionState.fallenRockCount }
		}

		// The last two states that were compared are also the first two states of the cycle, so find the start and end states of the cycle
		val firstHeightOfCycle = secondPushDirectionState.height - firstPushDirectionState.height
		val stateAtStartOfCycle = rockHeightsPerState[firstHeightOfCycle]!!
		val stateAtEndOfCycle = secondPushDirectionState

		// Add the remaining push direction sets that occured before the first cycle to the list
		for ((height, state) in rockHeightsPerState) {
			if (height == firstHeightOfCycle) break
			preCycleStates.add(state)
		}

		// Calculate the number of fallen rocks and their height during each cycle
		val cycleRockCount = stateAtEndOfCycle.fallenRockCount - stateAtStartOfCycle.fallenRockCount
		val cycleRockHeight = stateAtEndOfCycle.height - stateAtStartOfCycle.height

		// Calculate the number of fallen rocks and their height before the first cycle
		val preCycleRockCount = preCycleStates.sumOf { it.fallenRockCount }
		val preCycleHeight = preCycleStates.sumOf { it.height }

		// Calculate the total number of complete cycles and the total height reached after that number of complete cycles (plus the pre-cycle)
		val totalNumberOfCycles = totalRockCount / cycleRockCount
		val totalHeight = totalNumberOfCycles * cycleRockHeight + preCycleHeight

		// Calculate the total number of fallen rocks after all complete cycles (plus pre-cycle)
		val totalFallenRockCount = totalNumberOfCycles * cycleRockCount + preCycleRockCount

		// Calculate how many fallen rocks of the last cycle were above the requirement
		val overflowRockCount = totalFallenRockCount - totalRockCount

		// Calculate the height of the fallen rocks above the requirement by getting the state at the point where the overflow would start
		val heightAtOverflowStart = simulateRockFall(totalFallenRockCount) { state ->
			state.fallenRockCount == preCycleRockCount + (cycleRockCount - overflowRockCount)
		}.height
		val overflowRockHeight = (cycleRockHeight + preCycleHeight) - heightAtOverflowStart

		// The total height of the fallen rocks is the height of all (complete and pre-) cycles minus the overflow rock height
		return totalHeight - overflowRockHeight
	}

	private fun simulateRockFall(rockCount: Long, shouldStopSimulation: (SimulationState) -> Boolean = { false }): SimulationState {
		val fallenRocks = mutableListOf<RockShape>()
		var topMostEdge = 0L
		var fallenRockCount = 0L

		var pushDirectionIndex = 0
		(0 until rockCount).forEach { rockIndex ->
			// Determine the next rock shape that is falling down
			val rockShapeIndex = (rockIndex % 5).toInt()
			val nextRockShapePositions = rockShapes[rockShapeIndex]
			var fallingRockShape =
				RockShape(nextRockShapePositions).atStartingPosition(rockSpawnLeftMargin, topMostEdge - rockSpawnBottomMargin)

			var isRockResting = false
			while (!isRockResting) {
				// Create the current simulation state and check if the simulation should be stopped based on the current state
				val isStartOfPushDirections = pushDirectionIndex % pushDirections.size == 0
				val currentState = SimulationState(abs(topMostEdge), fallenRockCount, isStartOfPushDirections)
				if (shouldStopSimulation(currentState)) {
					return currentState
				}

				// Move rock in push direction
				val pushDirection = pushDirections[pushDirectionIndex % pushDirections.size]
				pushDirectionIndex++
				val rockShapeAfterPush = fallingRockShape.move(pushDirection)
				if (!rockShapeAfterPush.collidesWithAnything(fallenRocks.take(30), chamberWidth, 0)) {
					fallingRockShape = rockShapeAfterPush
				}

				// Move rock down
				val rockShapeAfterFalling = fallingRockShape.move(Direction.SOUTH)
				if (rockShapeAfterFalling.collidesWithAnything(fallenRocks.take(30), chamberWidth, 0)) {
					isRockResting = true
					fallenRocks.add(0, fallingRockShape)
				} else {
					fallingRockShape = rockShapeAfterFalling
				}
			}

			topMostEdge = fallingRockShape.xRange.first.coerceAtMost(topMostEdge)
			fallenRockCount++
		}

//		// Print the chamber
//		fallenRocks.map { it.shapePositions }
//			.flatten()
//			.associateWith { "#" }
//			.plus(Point(0, 0) to "+")
//			.plus(Point(0, 8) to "+")
//			.printAsGrid()

		return SimulationState(abs(topMostEdge), fallenRockCount, false)
	}

	private fun Char.toDirection() = when (this) {
		'<' -> Direction.WEST
		'>' -> Direction.EAST
		else -> throw IllegalArgumentException("Unknown jet push direction: $this")
	}

	private data class SimulationState(val height: Long, val fallenRockCount: Long, val isStartOfPushDirectionSet: Boolean)

	private data class RockShape(private val shapePositions: Set<Point>) {
		// The min and max of this shape on both axis
		val xRange = shapePositions.minOf { it.x }..shapePositions.maxOf { it.x }
		val yRange = shapePositions.minOf { it.y }..shapePositions.maxOf { it.y }

		/**
		 * @return A new rock shape moved to the correct starting position
		 */
		fun atStartingPosition(leftEdge: Int, bottomEdge: Long): RockShape {
			val deltaX = (bottomEdge - 1) - xRange.first - xRange.abs()
			val deltaY = (leftEdge + 1) - yRange.first
			return this.copy(shapePositions = shapePositions.map { it.move(deltaX, deltaY) }.toSet())
		}

		/**
		 * @return A new rock shape moved in [direction]
		 */
		fun move(direction: Direction) = this.copy(shapePositions = shapePositions.map { it.move(direction) }.toSet())

		/**
		 * @return True if this rock shape collides with either the chamber walls, the floor or one of the [otherRocks]
		 */
		fun collidesWithAnything(otherRocks: List<RockShape>, chamberWidth: Int, floor: Int = 0): Boolean {
			if (this.yRange.first <= 0) return true
			if (this.yRange.last > chamberWidth) return true
			if (this.xRange.last >= floor) return true
			return otherRocks.any { this.collidesWith(it) }
		}

		/**
		 * @return True if any of the shape positions of this or the [other] rock shape are the same
		 */
		fun collidesWith(other: RockShape) = this.shapePositions.any { other.shapePositions.contains(it) }
	}

}