package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Line
import ch.mikewong.adventofcode.common.models.Point
import kotlin.math.abs
import kotlin.math.max

class Day5 : Day<Int, Int>(2021, 5, "Hydrothermal Venture") {

	private val ventLines = inputLines.map { line ->
		val points = line.split(" -> ")
		val start = points.first().split(",")
		val end = points.last().split(",")
		Line(
			Point(start.first().toInt(), start.last().toInt()),
			Point(end.first().toInt(), end.last().toInt())
		)
	}
	private val sizeX = ventLines.maxOf { max(it.start.x, it.end.x) + 1 }
	private val sizeY = ventLines.maxOf { max(it.start.y, it.end.y) + 1 }

	override fun partOne(): Int {
		val grid = calculateGrid(ventLines.filter { it.isStraight() })
		return grid.sumOf { rows -> rows.count { it > 1 } }
	}

	override fun partTwo(): Int {
		val grid = calculateGrid(ventLines)
		return grid.sumOf { rows -> rows.count { it > 1 } }
	}

	private fun calculateGrid(ventLines: List<Line>): Array<IntArray> {
		val grid = Array(sizeY) { IntArray(sizeX) { 0 } }

		ventLines.forEach { line ->
			// Calculate the max distance between the start and end points
			val distance = max(abs(line.start.x - line.end.x), abs(line.start.y - line.end.y))

			(0..distance).forEach { offset ->
				// Calculate the new x and y values using the offset and the line slope for the x or y coordinate
				val x = line.start.x + (offset * line.xSlope())
				val y = line.start.y + (offset * line.ySlope())
				grid[y][x] += 1
			}
		}

		return grid
	}

}