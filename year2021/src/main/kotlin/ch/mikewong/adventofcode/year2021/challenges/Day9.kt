package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Point
import ch.mikewong.adventofcode.common.util.toIntGrid
import ch.mikewong.adventofcode.common.util.top

class Day9 : Day<Int, Int>(2021, 9, "Smoke Basin") {

	private val heightMap = inputLines.toIntGrid()

	override fun partOne(): Int {
		val lowPoints = findLowPoints()
		return lowPoints.sumOf { heightMap[it]!! + 1 }
	}

	override fun partTwo(): Int {
		val lowPoints = findLowPoints()
		val basinSizes = lowPoints.map { lowPoint ->
			var basinSize = 1

			// Keep a list of points to be checked (default to all adjacent points that are not 9) and points that were already checked
			val alreadyChecked = mutableSetOf(lowPoint)
			val pointsToCheck = filterAdjacentPoints(lowPoint) {
				heightMap[it] != null && heightMap[it] != 9
			}.toMutableSet()

			while (pointsToCheck.isNotEmpty()) {
				basinSize++

				// Mark this point as already checked and add its own adjacent points that are not 9 and not already checked to the list
				val point = pointsToCheck.first()
				alreadyChecked.add(point)
				pointsToCheck.remove(point)
				pointsToCheck.addAll(
					filterAdjacentPoints(point) {
						heightMap[it] != null && heightMap[it] != 9 && !alreadyChecked.contains(it)
					}
				)
			}

			basinSize
		}


		return basinSizes.top(3).reduce { acc, i -> acc * i }
	}

	private fun findLowPoints(): List<Point> {
		val lowPoints = mutableListOf<Point>()

		// Keep a list of points that still need to be checked. Default to all points
		val pointsToCheck = heightMap.keys.toMutableSet()

		// Iterate while there are still points to be checked
		while (pointsToCheck.isNotEmpty()) {
			// Get the adjacent points (that exist in the height map) of the next point to be checked
			val point = pointsToCheck.first()
			val adjacentPoints = filterAdjacentPoints(point) { heightMap[it] != null }

			val isLowerThanAdjacent = adjacentPoints.all { heightMap[it]!! > heightMap[point]!! }
			if (isLowerThanAdjacent) {
				// If the point is lower than all adjacent points, add it to the low point list and remove all adjacent points from the checking list
				lowPoints.add(point)
				pointsToCheck.removeAll(adjacentPoints)
			}

			pointsToCheck.remove(point)
		}

		return lowPoints
	}

	/**
	 * Filter the adjacent points of a specific [point] with a given predicate [filter]
	 */
	private fun filterAdjacentPoints(point: Point, filter: (Point) -> Boolean) = point.adjacent().filter(filter)
}