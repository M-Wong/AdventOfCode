package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Area
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day10 : Day<Int, Int>(2023, 10, "Pipe Maze") {

	private lateinit var startingPoint: Point
	private val pipes = inputLines.toGridNotNull { p, c ->
		when (c) {
			'S' -> {
				startingPoint = p
				Pipe(emptyList(), PipeType.VERTICAL) // This doesn't really matter, as it can only be determined after the rest of the pipes has been parsed
			}
			'|' -> Pipe(listOf(p.move(Direction.NORTH), p.move(Direction.SOUTH)), PipeType.VERTICAL)
			'-' -> Pipe(listOf(p.move(Direction.EAST), p.move(Direction.WEST)), PipeType.HORIZONTAL)
			'L' -> Pipe(listOf(p.move(Direction.NORTH), p.move(Direction.EAST)), PipeType.BOTTOM_LEFT_CORNER)
			'J' -> Pipe(listOf(p.move(Direction.NORTH), p.move(Direction.WEST)), PipeType.BOTTOM_RIGHT_CORNER)
			'7' -> Pipe(listOf(p.move(Direction.SOUTH), p.move(Direction.WEST)), PipeType.TOP_RIGHT_CORNER)
			'F' -> Pipe(listOf(p.move(Direction.SOUTH), p.move(Direction.EAST)), PipeType.TOP_LEFT_CORNER)
			else -> null
		}
	}.mapStartingPoint(startingPoint)

	override fun partOne(): Int {
		return findEnclosedLoop().size / 2
	}

	override fun partTwo(): Int {
		val loop = findEnclosedLoop()

		// Get the loop bounds (anything outside these bounds is not worth checking)
		val loopBounds = Area(
			topLeft = Point(
				loop.minOf { it.x },
				loop.minOf { it.y },
			),
			bottomRight = Point(
				loop.maxOf { it.x },
				loop.maxOf { it.y },
			)
		)

		// Get all points inside the area, except those that are part of the loop
		val pointsWithinArea = loopBounds.allPoints().filterNot { loop.contains(it) }

		return pointsWithinArea.count { p ->
			// Count the number of north facing pipes in the loop to the left of the point to be checked
			val northFacingPipes = (p.y downTo loopBounds.yRange.first).count { y ->
				val point = Point(p.x, y)
				if (loop.contains(point)) {
					val pipeType = pipes[point]?.type
					pipeType == PipeType.VERTICAL || pipeType == PipeType.BOTTOM_LEFT_CORNER || pipeType == PipeType.BOTTOM_RIGHT_CORNER
				} else {
					false
				}
			}

			// Any point that crosses an odd number of north facing pipes is inside the bounds, an even number is outside the bounds
			northFacingPipes % 2 == 1
		}
	}

	private fun Map<Point, Pipe>.mapStartingPoint(point: Point) = this.toMutableMap().apply {
		// Find the pipes that connect to the starting point
		val pointsConnectedToStartingPipe = startingPoint.adjacent { p ->
			this[p]?.connects?.contains(startingPoint) ?: false
		}

		// Determine the pipe type of the starting point based on the connecting pipes and their types
		val startingPipeType = when {
			pointsConnectedToStartingPipe.all { it.x == startingPoint.x } -> PipeType.HORIZONTAL
			pointsConnectedToStartingPipe.all { it.y == startingPoint.y } -> PipeType.VERTICAL
			startingPoint.move(Direction.SOUTH) in pointsConnectedToStartingPipe -> {
				if (startingPoint.move(Direction.EAST) in pointsConnectedToStartingPipe) {
					PipeType.TOP_LEFT_CORNER
				} else {
					PipeType.TOP_RIGHT_CORNER
				}
			}
			startingPoint.move(Direction.NORTH) in pointsConnectedToStartingPipe -> {
				if (startingPoint.move(Direction.EAST) in pointsConnectedToStartingPipe) {
					PipeType.BOTTOM_LEFT_CORNER
				} else {
					PipeType.BOTTOM_RIGHT_CORNER
				}
			}
			else -> throw IllegalStateException("Can't match a PipeType for starting point")
		}
		val startingPipe = Pipe(pointsConnectedToStartingPipe, startingPipeType)
		set(point, startingPipe)
	}.toMap()

	private fun findEnclosedLoop() = buildSet {
		add(startingPoint)

		// Add all the connecting pipes beginning at the starting point
		var next = pipes.getValue(startingPoint).connects
		while (next.isNotEmpty()) {
			addAll(next)
			next = next.mapNotNull { pipes[it] }.flatMap { pipe -> pipe.connects.filterNot { this.contains(it) } }
		}
	}

	private data class Pipe(val connects: List<Point>, val type: PipeType)

	private enum class PipeType {
		VERTICAL,
		HORIZONTAL,
		TOP_LEFT_CORNER,
		TOP_RIGHT_CORNER,
		BOTTOM_LEFT_CORNER,
		BOTTOM_RIGHT_CORNER,
	}

}