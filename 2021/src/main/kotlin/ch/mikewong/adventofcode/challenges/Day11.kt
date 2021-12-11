package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.models.Point
import ch.mikewong.adventofcode.util.toIntGrid

class Day11 : Day<Int, Int>(11, "Dumbo Octopus") {

	private val octopusGrid = inputLines.toIntGrid()

	override fun partOne(): Int {
		var flashCount = 0
		var grid = octopusGrid.toMutableMap()

		(1..100).forEach { step ->
			grid = grid.map { it.key to it.value + 1 }.toMap().toMutableMap()

			val alreadyFlashed = mutableSetOf<Point>()
			val aboutToFlash = grid.filter { it.value > 9 }.keys.toMutableSet()
			while (aboutToFlash.isNotEmpty()) {
				flashCount++

				val octopus = aboutToFlash.first()
				octopus.surrounding().filter { grid[it] != null && !alreadyFlashed.contains(it) }.forEach {
					val newValue = grid[it]!! + 1
					grid[it] = newValue

					if (newValue > 9 && !alreadyFlashed.contains(it)) {
						aboutToFlash.add(it)
					}
				}

				aboutToFlash.remove(octopus)
				alreadyFlashed.add(octopus)
				grid[octopus] = 0
			}
		}

		return flashCount
	}

	override fun partTwo(): Int {
		var step = 1
		var grid = octopusGrid.toMutableMap()

		while (true) {
			grid = grid.map { it.key to it.value + 1 }.toMap().toMutableMap()

			val alreadyFlashed = mutableSetOf<Point>()
			val aboutToFlash = grid.filter { it.value > 9 }.keys.toMutableSet()
			while (aboutToFlash.isNotEmpty()) {
				val octopus = aboutToFlash.first()
				octopus.surrounding().filter { grid[it] != null && !alreadyFlashed.contains(it) }.forEach {
					val newValue = grid[it]!! + 1
					grid[it] = newValue

					if (newValue > 9 && !alreadyFlashed.contains(it)) {
						aboutToFlash.add(it)
					}
				}

				aboutToFlash.remove(octopus)
				alreadyFlashed.add(octopus)
				grid[octopus] = 0
			}

			if (grid.all { it.value == 0 }) {
				break
			}

			step++
		}

		return step
	}

}