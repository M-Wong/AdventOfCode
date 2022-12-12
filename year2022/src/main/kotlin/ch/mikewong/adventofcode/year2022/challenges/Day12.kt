package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toGrid
import ch.mikewong.adventofcode.common.models.Point

class Day12 : Day<Int, Int>(2022, 12, "Hill Climbing Algorithm") {

	private lateinit var start: Point
	private lateinit var end: Point
	private val graph = readInput()

	override fun partOne(): Int {
		return findShortestPath(
			from = start, // Find the shortest path from the start point...
			isTargetNode = { it == end }, // ... and to the end point
			neighbours = { point ->
				// Neighbours are adjacent points that are at most 1 higher than the current point
				val reference = graph[point]!!
				point.adjacent { graph[it] != null && (graph[it]!! - reference) <= 1 }
			}
		)
	}

	override fun partTwo(): Int {
		return findShortestPath(
			from = end,
			isTargetNode = { graph[it] == 0 },
			neighbours = { point ->
				// Neighbours are adjacent points that are at most 1 lower than the current point
				val reference = graph[point]!!
				point.adjacent { graph[it] != null && (reference - graph[it]!!) <= 1 }
			}
		)
	}

	private fun readInput(): Map<Point, Int> {
		// Map the input to a point grid and convert the character to a height level, while also marking the start (S) and end (E)
		return inputLines.toGrid { p, c ->
			when (c) {
				'S' -> {
					start = p
					0
				}
				'E' -> {
					end = p
					26
				}
				else -> c - 'a'
			}
		}
	}

	private fun findShortestPath(from: Point, isTargetNode: (Point) -> Boolean, neighbours: (Point) -> List<Point>): Int {
		// Keep track of the unvisited nodes and the minimum distances for visited nodes
		val unvisitedNodes = graph.keys.toMutableSet()
		val distances = unvisitedNodes.associateWith { Int.MAX_VALUE }.toMutableMap()
		distances[from] = 0


		var targetNode: Point? = null

		while (unvisitedNodes.isNotEmpty()) {
			// While there are unvisited nodes left, get the next unvisited node with the lowest currently known distance
			val nextNode = unvisitedNodes.minBy { distances[it] ?: 0 }
			unvisitedNodes.remove(nextNode)

			// If the caller decides the next node is the target node, save it and stop the loop
			if (isTargetNode.invoke(nextNode)) {
				targetNode = nextNode
				break
			}

			// Otherwise, iterate its neighbours and update their distances (with a weight of 1)
			neighbours.invoke(nextNode).forEach {
				val alternativeDistance = (distances[nextNode] ?: 0) + 1
				if (alternativeDistance < (distances[it] ?: 0)) {
					distances[it] = alternativeDistance
				}
			}
		}

		return requireNotNull(distances[targetNode])
	}

}