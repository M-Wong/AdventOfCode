package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toCharGrid
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day12 : Day<Int, Int>(2024, 12, "Garden Groups") {

	private val map by lazy { inputLines.toCharGrid() }
	private val regions by lazy { map.findRegions() }

	override fun partOne(): Int {
		return regions.sumOf { it.area * it.perimeter }
	}

	override fun partTwo(): Int {
		return regions.sumOf { it.area * it.sides }
	}

	private fun Map<Point, Char>.findRegions(): List<Region> {
		val unvisitedPlots = this.keys.toMutableList()
		val regions = mutableListOf<Region>()

		while (unvisitedPlots.isNotEmpty()) {
			val initial = unvisitedPlots.removeFirst()
			val plantType = map.getValue(initial)

			val garden = mutableSetOf(initial)

			// From the initial plot, find all connecting plots and add them to the garden
			var nextPlots = initial.adjacent { map[it] == plantType }.toSet()
			while (nextPlots.isNotEmpty()) {
				garden.addAll(nextPlots)
				nextPlots = nextPlots.flatMap { plot -> plot.adjacent { map[it] == plantType && it !in garden } }.toSet()
			}

			// Remove all plots in this garden from the list of unvisited plots and add the region
			unvisitedPlots.removeAll(garden)
			regions.add(Region(garden))
		}

		return regions
	}

	data class Region(val plots: Set<Point>) {
		val area by lazy { plots.size }

		val perimeter by lazy {
			// The perimeter is the sum of each plot's adjacent sides not part of the same region
			plots.sumOf { plot -> plot.adjacent { it !in plots }.size }
		}

		val sides by lazy {
			// The number of sides is the same as the number of corners
			plots.sumOf { plot ->
				var cornerCount = 0

				// Check for outer corners
				if (plot.move(Direction.NORTH) !in plots && plot.move(Direction.EAST) !in plots) {
					// Top right corner
					cornerCount++
				}
				if (plot.move(Direction.EAST) !in plots && plot.move(Direction.SOUTH) !in plots) {
					// Bottom right corner
					cornerCount++
				}
				if (plot.move(Direction.SOUTH) !in plots && plot.move(Direction.WEST) !in plots) {
					// Bottom left corner
					cornerCount++
				}
				if (plot.move(Direction.WEST) !in plots && plot.move(Direction.NORTH) !in plots) {
					// Top left corner
					cornerCount++
				}

				// Check for inner corners
				if (plot.move(Direction.NORTH) in plots && plot.move(Direction.EAST) in plots && plot.move(Direction.NORTH_EAST) !in plots) {
					// Bottom left corner
					cornerCount++
				}
				if (plot.move(Direction.EAST) in plots && plot.move(Direction.SOUTH) in plots && plot.move(Direction.SOUTH_EAST) !in plots) {
					// Top left corner
					cornerCount++
				}
				if (plot.move(Direction.SOUTH) in plots && plot.move(Direction.WEST) in plots && plot.move(Direction.SOUTH_WEST) !in plots) {
					// Top right corner
					cornerCount++
				}
				if (plot.move(Direction.WEST) in plots && plot.move(Direction.NORTH) in plots && plot.move(Direction.NORTH_WEST) !in plots) {
					// Bottom right corner
					cornerCount++
				}

				cornerCount
			}
		}
	}

}