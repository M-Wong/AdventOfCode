package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.algorithms.dijkstra
import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toIntGrid
import ch.mikewong.adventofcode.common.models.Area
import ch.mikewong.adventofcode.common.models.Point

class Day15 : Day<Int, Int>(2021, 15, "Chiton") {

	private val riskGrid = inputLines.toIntGrid()
	private val startPoint = Point(0, 0)

	override fun partOne(): Int {
		val endPoint = Point(riskGrid.maxOf { it.key.x }, riskGrid.maxOf { it.key.y })
		val bounds = inputSize.toArea()

		return dijkstra(
			startingNode = startPoint,
			isTargetNode = { it == endPoint },
			neighbours = { current ->
				current.adjacent { bounds.contains(it) }
			},
			costFunction = { _, next -> riskGrid.getValue(next) },
		).totalCost
	}

	override fun partTwo(): Int {
		val size = riskGrid.maxOf { it.key.x } + 1
		val expandedGrid = expandGrid(riskGrid, size.toInt())
		val endPoint = Point(expandedGrid.maxOf { it.key.x }, expandedGrid.maxOf { it.key.y })
		val bounds = Area(Point(0, 0), endPoint)

		return dijkstra(
			startingNode = startPoint,
			isTargetNode = { it == endPoint },
			neighbours = { current ->
				current.adjacent { bounds.contains(it) }
			},
			costFunction = { _, next -> expandedGrid.getValue(next) },
		).totalCost
	}

	private fun expandGrid(grid: Map<Point, Int>, size: Int): Map<Point, Int> {
		val expandedGrid = grid.toMutableMap()
		val expandedSize = size * 5
		(0 until expandedSize).forEach { x ->
			(0 until expandedSize).forEach { y ->
				val originalValue = expandedGrid[Point(x % size, y % size)] ?: 0
				var newValue = originalValue
				val increment = (x / size) + (y / size)
				repeat(increment) {
					newValue++
					if (newValue > 9) newValue = 1
				}
				expandedGrid[Point(x, y)] = if (newValue > 9) 1 else newValue
			}
		}

		return expandedGrid
	}

}