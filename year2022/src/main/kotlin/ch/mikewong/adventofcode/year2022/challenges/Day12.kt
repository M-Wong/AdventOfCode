package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.algorithms.dijkstra
import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGrid
import ch.mikewong.adventofcode.common.models.Point

class Day12 : Day<Int, Int>(2022, 12, "Hill Climbing Algorithm") {

	private lateinit var start: Point
	private lateinit var end: Point
	private val graph = readInput()

	override fun partOne(): Int {
		return dijkstra(
			startingNode = start,
			isTargetNode = { it == end },
			neighbours = { current ->
				// Neighbours are adjacent points that are at most 1 higher than the current point
				val reference = graph.getValue(current)
				current.adjacent { graph[it] != null && (graph[it]!! - reference) <= 1 }
			}
		).totalCost
	}

	override fun partTwo(): Int {
		return dijkstra(
			startingNode = end,
			isTargetNode = { graph[it] == 0},
			neighbours = { current ->
				// Neighbours are adjacent points that are at most 1 lower than the current point
				val reference = graph.getValue(current)
				current.adjacent { graph[it] != null && (reference - graph[it]!!) <= 1 }
			}
		).totalCost
	}

	private fun readInput(): Map<Point, Int> {
		// Map the input to a point grid and convert the character to a height level, while also marking the start (S) and end (E)
		return inputLines.toGrid { p, c ->
			when (c) {
				'S' -> 0.also { start = p }
				'E' -> 26.also { end = p }
				else -> c - 'a'
			}
		}
	}

}