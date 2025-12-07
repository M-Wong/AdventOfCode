package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.increment
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day7 : Day<Int, Long>(2025, 7, "Laboratories") {

	private val initialManifold by lazy { parseInput() }
	private val beamSplitters by lazy { initialManifold.beamSplitters }

	/** 1533 */
	override fun partOne(): Int {
		var currentBeamPositions = initialManifold.beamPositions.associateWith { 1L }
		var totalSplits = 0

		repeat(inputSize.height.toInt()) {
			val result = moveBeams(currentBeamPositions)
			totalSplits += result.splits
			currentBeamPositions = result.beamPositions
		}

		return totalSplits
	}

	/** 10733529153890 */
	override fun partTwo(): Long {
		var currentBeamPositions = initialManifold.beamPositions.associateWith { 1L }

		repeat(inputSize.height.toInt()) {
			val result = moveBeams(currentBeamPositions)
			currentBeamPositions = result.beamPositions
		}

		return currentBeamPositions.values.sum()
	}

	private fun parseInput(): TachyonManifold {
		val beamPositions = mutableSetOf<Point>()
		val beamSplitters = inputLines.toGridNotNull { point, c ->
			if (c == 'S') beamPositions.add(point)
			c.takeIf { it == '^' }
		}.keys

		return TachyonManifold(
			beamPositions = beamPositions,
			beamSplitters = beamSplitters,
		)
	}

	private fun moveBeams(currentBeamPositions: Map<Point, Long>): BeamResult {
		val nextBeamPositions = mutableMapOf<Point, Long>()
		var splits = 0

		currentBeamPositions.forEach { (point, timelines) ->
			// For each current beam position, calculate the next point this beam would move
			val nextPoint = point.move(Direction.SOUTH)
			if (nextPoint in beamSplitters) {
				// If the next point is a beam splitter, count the split
				splits++

				// Split the beam into a left and right timeline, adding their current timeline value to that point if it already exists
				val leftPoint = nextPoint.move(Direction.WEST)
				nextBeamPositions.increment(leftPoint, timelines)

				val rightPoint = nextPoint.move(Direction.EAST)
				nextBeamPositions.increment(rightPoint, timelines)
			} else {
				// Beam is not split, but might be joining other timelines
				nextBeamPositions.increment(nextPoint, timelines)
			}
		}

		return BeamResult(
			beamPositions = nextBeamPositions,
			splits = splits,
		)
	}

	private data class BeamResult(
		val beamPositions: Map<Point, Long>,
		val splits: Int,
	)

	private data class TachyonManifold(
		val beamPositions: Set<Point>,
		val beamSplitters: Set<Point>,
	)

}