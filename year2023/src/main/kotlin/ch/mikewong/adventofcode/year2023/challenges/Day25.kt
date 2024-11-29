package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.algorithms.dijkstra
import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toPair
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

class Day25 : Day<Int, Int>(2023, 25, "Snowverload") {

	private val componentGraph = readInput()

	// 527790
	// Vertices to remove: [(pzq, rrz), (ddj, znv), (jtr, mtq)]
	override fun partOne(): Int {
		// Get the most visited edges within the graph
		val mostVistedEdges = nonDeterministicEdgeRemoval()

		val vertices = componentGraph.vertices.toMutableSet()
		val edges = componentGraph.edges.toMutableSet()

		mostVistedEdges.forEach { (from, to) ->
			// Remove the most visited edges from the list
			edges.removeIf { (it.from == from || it.to == from) && (it.from == to || it.to == to) }
		}

		// Start at a random vertex and traverse all reachable edges
		val start = vertices.random()
		val seen = mutableSetOf<String>()
		val queue = mutableListOf(start)

		while (queue.isNotEmpty()) {
			val vertex = queue.removeFirst()
			seen.add(vertex)
			val next = edges.mapNotNull { edge ->
				when {
					edge.from == vertex -> edge.to.takeIf { !seen.contains(it) }
					edge.to == vertex -> edge.from.takeIf { !seen.contains(it) }
					else -> null
				}
			}
			queue.addAll(next)
		}

		// Total vertices: 1453
		// Seen vertices: 730
		// Remaining vertices: 723
		println("Total vertices: ${vertices.size}")
		println("Seen vertices: ${seen.size}")
		println("Remaining vertices: ${(vertices.size - seen.size)}")

		// If the graph was correctly split into two subgraphs, the size of the seen set will be the size of one subgraph and the total number of vertices minus that size will be the other subgraph
		return (vertices.size - seen.size) * seen.size
	}

	override fun partTwo(): Int {
		return 0
	}

	private fun readInput(): UndirectedGraph<String> {
		val vertices = mutableSetOf<String>()
		val edges = mutableSetOf<Edge<String>>()
		inputLines.forEach { line ->
			val (name, connections) = line.split(": ")
			vertices.add(name)
			connections.split(" ").forEach { connection ->
				vertices.add(connection)
				edges.add(Edge(name, connection, 1))
			}
		}

		return UndirectedGraph(vertices, edges)
	}

	/**
	 * Returns a non-deterministic list of edges that are most likely to be the three edges to be removed
	 */
	private fun nonDeterministicEdgeRemoval(): List<Pair<String, String>> {
		val visitedCount = mutableMapOf<Pair<String, String>, Int>().withDefault { 0 }

		// Needs to run enough times to be moderately accurate
		repeat(1000) {
			val randomStart = componentGraph.vertices.random()
			val randomEnd = componentGraph.vertices.minus(randomStart).random()
			val shortestPath = componentGraph.findShortestPath(randomStart, randomEnd)
			shortestPath.vertices.zipWithNext().forEach { (from, to) ->
				val key = listOf(from, to).sorted().toPair()
				visitedCount[key] = visitedCount.getValue(key) + 1
			}
		}

		return visitedCount.entries.sortedByDescending { it.value }.take(3).map { it.key }
	}

	private data class Edge<T>(val from: T, val to: T, val distance: Int)

	private class UndirectedGraph<T>(val vertices: Set<T>, val edges: Set<Edge<T>>) {

		// Calculate an adjacency matrix between every connected vertex and the distance between them
		private val adjacencies = vertices.associateWith { vertex ->
			edges.mapNotNull { edge ->
				when {
					edge.from == vertex -> edge.to to edge.distance
					edge.to == vertex -> edge.from to edge.distance
					else -> null
				}
			}.toMap()
		}

		fun findShortestPath(from: T, target: T): Path<T> {
			val result = dijkstra(
				startingNode = from,
				isTargetNode = { it == target},
				neighbours = { current ->
					adjacencies.getValue(current).keys.toList()
				},
				costFunction = { current, next ->
					adjacencies.getValue(current).getValue(next)
				}
			)

			return Path(vertices = result.path, totalDistance = result.totalCost)
		}

		data class Path<T>(val vertices: List<T>, val totalDistance: Int)
	}

}