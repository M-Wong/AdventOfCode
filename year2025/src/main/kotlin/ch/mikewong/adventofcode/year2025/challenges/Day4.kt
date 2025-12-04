package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGridNotNull
import ch.mikewong.adventofcode.common.models.Point

class Day4 : Day<Int, Int>(2025, 4, "Printing Department") {

	private val paperRolls by lazy {
		inputLines.toGridNotNull { _, char -> char.takeIf { it == '@' } }.keys
	}

	/** 1508 */
	override fun partOne(): Int {
		return paperRolls.getRemovableRolls().size
	}

	/** 8538 */
	override fun partTwo(): Int {
		val allRemovedRolls = getAllRemovableRolls(paperRolls, emptySet())

		return allRemovedRolls.size
	}

	private fun Set<Point>.getRemovableRolls(): Set<Point> {
		return filter { roll ->
			roll.surrounding { it in this }.size < 4
		}.toSet()
	}

	private fun getAllRemovableRolls(availableRolls: Set<Point>, removedRolls: Set<Point>): Set<Point> {
		val newlyRemovedRolls = availableRolls.getRemovableRolls()
		return if (newlyRemovedRolls.isEmpty()) {
			// If no more rolls can be removed, we're done and can return the removed rolls
			removedRolls
		} else {
			// If more rolls can be removed, remove them from the available rolls and recursively continue
			getAllRemovableRolls(availableRolls - newlyRemovedRolls, removedRolls + newlyRemovedRolls)
		}
	}

}