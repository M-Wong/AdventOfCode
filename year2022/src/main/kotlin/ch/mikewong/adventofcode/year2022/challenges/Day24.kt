package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Area
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day24 : Day<Int, Int>(2022, 24, "Blizzard Basin") {

	private val initialMapState by lazy { readInput() }

	private val startingPoint by lazy {
		val col = inputLines.first().indexOf('.')
		Point(0, col)
	}

	private val endingPoint by lazy {
		val col = inputLines.last().indexOf('.')
		Point(inputLines.lastIndex, col)
	}

	override fun partOne(): Int {
		val (time, _) = calculateTime(startingPoint, endingPoint, initialMapState)
		return time
	}

	override fun partTwo(): Int {
		val (timeThere, thereMapState) = calculateTime(startingPoint, endingPoint, initialMapState)
		val (timeBack, backMapState) = calculateTime(endingPoint, startingPoint, thereMapState)
		val (timeThereAgain, _) = calculateTime(startingPoint, endingPoint, backMapState)

		return timeThere + timeBack + timeThereAgain
	}

	private fun calculateTime(start: Point, destination: Point, initialMapState: MapState): Pair<Int, MapState> {
		var currentMapState = initialMapState
		val positions = mutableSetOf(start)
		var minute = 1

		while (true) {
			// Calculate the next map state
			currentMapState = currentMapState.calculateNextMapState()

			// Get a list of the next possible positions (adjacent plus current) that are within the map area and not covered by a blizzard
			val nextPossiblePositions = positions.flatMap { pos ->
				pos.adjacent().plus(pos).filter { p ->
					p == start || p == destination || (p in currentMapState.area && currentMapState.blizzards.getValue(p).isEmpty())
				}
			}

			if (nextPossiblePositions.contains(destination)) {
				// If one of the next possible positions is the destination,
				return minute to currentMapState
			} else {
				positions.clear()
				positions.addAll(nextPossiblePositions)
			}

			minute++
		}
	}

	private fun MapState.calculateNextMapState(): MapState {
		val newBlizzardPositions = mutableMapOf<Point, List<Direction>>().withDefault { mutableListOf() }

		this.blizzards.forEach { (point, directions) ->
			directions.forEach { direction ->
				// Move the blizzards in their direction and wrap around the map range
				val nextPoint = point.move(direction).wrapAround(area)
				newBlizzardPositions[nextPoint] = newBlizzardPositions.getValue(nextPoint).plus(direction)
			}
		}

		return this.copy(blizzards = newBlizzardPositions)
	}

	private fun readInput(): MapState {
		val blizzards = mutableMapOf<Point, List<Direction>>().withDefault { mutableListOf() }

		inputLines.forEachIndexed { row, line ->
			line.forEachIndexed { col, c ->
				val point = Point(row, col)
				val direction = when (c) {
					'^' -> Direction.NORTH
					'>' -> Direction.EAST
					'v' -> Direction.SOUTH
					'<' -> Direction.WEST
					else -> null
				}

				direction?.let { blizzards[point] = blizzards.getValue(point).plus(it) }
			}
		}

		// The map is surrounded by walls (except for start and end), so the area is from (1, 1) to (height - 1, width - 1)
		val area = Area(
			topLeft = Point(1, 1),
			bottomRight = Point(inputLines.lastIndex - 1, inputLines.first().lastIndex - 1)
		)

		return MapState(area, blizzards)
	}

	data class MapState(val area: Area, val blizzards: Map<Point, List<Direction>>)
}