package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allPairs
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Point

class Day11 : Day<Int, Long>(2023, 11, "Cosmic Expansion") {

	private val galaxies = inputLines.toGridNotNull { _, c -> c.takeIf { it == '#' } }.keys

	override fun partOne(): Int {
		return expandGalaxies(2).allPairs().sumOf { (a, b) -> a.manhattanDistanceTo(b).toInt() }
	}

	override fun partTwo(): Long {
		val expansionRate = if (isControlSet) 100 else 1000000
		return expandGalaxies(expansionRate).allPairs().sumOf { (a, b) -> a.manhattanDistanceTo(b) }
	}

	private fun expandGalaxies(expansionRate: Int): List<Point> {
		// Find the indices of the empty rows and columns
		val emptyRows = inputSize.rowRange().filter { row -> galaxies.none { it.x == row } }
		val emptyCols = inputSize.colRange().filter { col -> galaxies.none { it.y == col } }

		return galaxies.map { galaxy ->
			// Map each galaxy to a new coordinate, with a delta X and Y equal to the number of empty rows/columns before multiplied by the expansion rate
			// The minus one on the expansion rate is to account for the already existing empty row/column in the coordinates
			val deltaX = emptyRows.count { it < galaxy.x } * (expansionRate - 1)
			val deltaY = emptyCols.count { it < galaxy.y } * (expansionRate - 1)
			galaxy.move(deltaX, deltaY)
		}
	}

}