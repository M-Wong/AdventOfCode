package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allPairs
import ch.mikewong.adventofcode.common.models.Point3D
import ch.mikewong.adventofcode.common.models.UndirectedGraph

class Day8 : Day<Int, Int>(2025, 8, "Playground") {

	private val junctionBoxes by lazy {
		inputLines.map { line ->
			val (x, y, z) = line.split(",").map { it.toInt() }
			Point3D(x, y, z)
		}
	}

	/** 63920 */
	override fun partOne(): Int {
		val connections = if (isControlSet) 10 else 1000

		val allPairs = junctionBoxes.allPairs()
		val pairsSortedByDistance = allPairs.sortedBy { (a, b) -> a.distanceTo(b) }

		val edges = pairsSortedByDistance.take(connections)
		val graph = UndirectedGraph(edges)
		val components = graph.connectedComponents()

		return components
			.map { it.size }
			.sortedDescending()
			.take(3)
			.reduce { acc, size -> acc * size }
	}

	/** 1026594680 */
	override fun partTwo(): Int {
		val unconnectedBoxes = junctionBoxes.toMutableSet()

		val allPairs = junctionBoxes.allPairs()
		val edges = allPairs.sortedBy { (a, b) -> a.distanceTo(b) }.toMutableList()

		while (unconnectedBoxes.isNotEmpty()) {
			val (a, b) = edges.removeAt(0)

			unconnectedBoxes.remove(a)
			unconnectedBoxes.remove(b)

			if (unconnectedBoxes.isEmpty()) {
				return a.x * b.x
			}
		}

		throw IllegalStateException("Failed to connect all boxes")
	}

}