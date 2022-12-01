package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Point
import ch.mikewong.adventofcode.common.util.toIntGrid
import java.util.*

class Day15 : Day<Int, Int>(2021, 15, "Chiton") {

	private val riskGrid = inputLines.toIntGrid()
	private val startPoint = Point(0, 0)

	override fun partOne(): Int {
		val endPoint = Point(riskGrid.maxOf { it.key.x }, riskGrid.maxOf { it.key.y })
		return shortestPath(riskGrid, startPoint, endPoint)
	}

	override fun partTwo(): Int {
		val size = riskGrid.maxOf { it.key.x } + 1
		val expandedGrid = expandGrid(riskGrid, size)
		val endPoint = Point(expandedGrid.maxOf { it.key.x }, expandedGrid.maxOf { it.key.y })
		return shortestPath(expandedGrid, startPoint, endPoint)
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

	private fun shortestPath(grid: Map<Point, Int>, start: Point, end: Point): Int {
		val visited = mutableSetOf(start)
		val queue = PriorityQueue<Path>()
		queue.add(Path(start, setOf(start), 0))

		while (queue.isNotEmpty()) {
			val path = queue.poll()
			if (path.start == end) {
				return path.risk
			}

			path.start.adjacent { it.x in 0..end.x && it.y in 0..end.y && !visited.contains(it)  }.forEach { next ->
				visited.add(next)
				val newPath = path.nodes + next
				queue.add(Path(next, newPath, path.risk + (grid[next] ?: 0)))
			}
		}

		return -1
	}

	private data class Path(val start: Point, val nodes: Set<Point>, val risk: Int) : Comparable<Path> {
		override fun compareTo(other: Path): Int {
			return this.risk.compareTo(other.risk)
		}
	}

}