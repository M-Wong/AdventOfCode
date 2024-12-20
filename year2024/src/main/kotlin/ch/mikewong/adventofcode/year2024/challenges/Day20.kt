package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.algorithms.dijkstra
import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGridNotNull

class Day20 : Day<Int, Int>(2024, 20, "Race Condition") {

	private val walls by lazy { inputLines.toGridNotNull { _, c -> c.takeIf { it == '#' } } }
	private val start by lazy { inputLines.toGridNotNull { _, c -> c.takeIf { it == 'S' } }.keys.single() }
	private val end by lazy { inputLines.toGridNotNull { _, c -> c.takeIf { it == 'E' } }.keys.single() }

	private val regularPath by lazy {
		dijkstra(
			startingNode = start,
			isTargetNode = { it == end },
			neighbours = { current ->
				current.adjacent { it !in walls }
			}
		)
	}

	// 1438
	override fun partOne(): Int {
		val minSavings = if (isControlSet) 10 else 100
		return countShortcuts(maxShortcutSteps = 2, minSavings = minSavings)
	}

	// 1026446
	override fun partTwo(): Int {
		val minSavings = if (isControlSet) 70 else 100
		return countShortcuts(maxShortcutSteps = 20, minSavings = minSavings)
	}

	private fun countShortcuts(maxShortcutSteps: Int, minSavings: Int): Int {
		val path = regularPath.paths.single().plus(end)

		val allShortcutSavings = path.flatMapIndexed { idx, current ->
			// Don't need to check the next nodes that are less than minSavings distance away, even if we cheat to get there, we won't save enough
			val nextToConsider = (idx + minSavings).coerceAtMost(path.lastIndex)
			val remaining = path.subList(nextToConsider, path.lastIndex)

			// Find all remaining points, where the Manhattan Distance is within the range of the allowed shortcut steps and the savings are at least minSavings
			remaining
				.associateWith { other -> current.manhattanDistanceTo(other) }
				.filter { (_, manhattanDistance) -> manhattanDistance in 2..maxShortcutSteps }
				.map { (other, manhattanDistance) ->
					val regularDistance = path.indexOf(other) - idx
					regularDistance - manhattanDistance
				}
				.filter { it >= minSavings }
		}

		return allShortcutSavings.size
	}

}