package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.Graph
import ch.mikewong.adventofcode.common.util.isAllLowerCase
import ch.mikewong.adventofcode.common.util.isAllUpperCase

class Day12 : Day<Int, Int>(2021, 12, "Passage Pathing") {

	private val paths = inputLines.map { it.split('-') }.map { it.first() to it.last() }
	private val graph = Graph(paths)

	override fun partOne() = graph.findPaths("start", "end", false).size

	override fun partTwo() = graph.findPaths("start", "end", true).size

	private fun Graph.findPaths(
		from: String,
		to: String,
		allowSmallCaveRevisit: Boolean,
		path: List<String> = emptyList(),
	): Set<List<String>> {
		val result = mutableSetOf<List<String>>()
		val newPath = path + from

		// Iterate the connecting nodes of this from node
		getConnectingNodes(from).forEach { next ->
			when {
				next == to -> {
					// Arrived at the ending point
					result.add(newPath + to)
				}
				from.isAllUpperCase() -> {
					// From node is a big cave. Find all paths from the next node to the end node
					result.addAll(findPaths(next, to, allowSmallCaveRevisit, newPath))
				}
				from.isAllLowerCase() -> {
					// From node is a small cave. Find all paths from the next node to the end node, not including this small cave
					result.addAll(
						filterNode(from).findPaths(
							next,
							to,
							allowSmallCaveRevisit,
							newPath
						)
					)

					// If a small cave revisit is still allowed, find all paths from the next node to the end node, including this small cave
					if (allowSmallCaveRevisit && from != "start") {
						result.addAll(findPaths(next, to, false, newPath))
					}
				}
			}
		}

		return result.toSet()
	}
}