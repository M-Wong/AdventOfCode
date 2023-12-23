package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.algorithms.dijkstra
import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allPairs
import ch.mikewong.adventofcode.common.extensions.printAsCharGrid
import ch.mikewong.adventofcode.common.extensions.set
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point
import java.util.*

class Day23 : Day<Int, Int>(2023, 23, "A Long Walk") {

	private val paths = mutableSetOf<Point>()
	private val forest = mutableSetOf<Point>()
	private val slopes = mutableMapOf<Point, Direction>()
	private val startingPoint = Point(0, 1)
	private val endingPoint = Point(inputSize.height - 1, inputSize.width - 2)

	init {
		inputLines.forEachIndexed { row, line ->
			line.forEachIndexed { column, char ->
				val point = Point(row, column)
				when (char) {
					'.' -> paths.add(point)
					'#' -> forest.add(point)
					'^' -> slopes[point] = Direction.NORTH
					'>' -> slopes[point] = Direction.EAST
					'v' -> slopes[point] = Direction.SOUTH
					'<' -> slopes[point] = Direction.WEST
				}
			}
		}
		paths.addAll(slopes.keys)
	}

	override fun partOne(): Int {
		val result = findLongestPath(
			start = startingPoint,
			target = endingPoint,
			neighbours = { current ->
				// Valid neighbours for the current position are all adjacent points that are paths and are in the same direction as the slope on that position (if any)
				Direction.lateral().mapNotNull { direction ->
					val next = current.move(direction)
					next.takeIf {
						val slope = slopes[it] ?: direction
						paths.contains(it) && slope == direction
					}
				}
			}
		)
		return result.totalCost
	}

	override fun partTwo(): Int {
		// Find all junctions in the map (junctions are all points that have more than two neighbouring path tiles)
		val junctions = paths.filter { point ->
			point.adjacent { paths.contains(it) }.size > 2
		}

		// The vertices in the graph are all junctions plus the starting and ending point
		val vertices = junctions.plus(startingPoint).plus(endingPoint)

		// Calculate the distances between each junction (the edges in the graph) using Dijkstra
		val edges = vertices.allPairs().mapNotNull { (from, to) ->
			try {
				dijkstra(
					startingNode = from,
					isTargetNode = { it == to },
					neighbours = { current ->
						current.adjacent {
							paths.contains(it) && !vertices.minus(from).minus(to).contains(it)
						}
					}
				)
			} catch (e: Exception) {
				null
			}
		}.map { Edge(it.start, it.target, it.totalCost) }

		// Create an undirected graph with the calculated vertices and edges
		val graph = UndirectedGraph(vertices.toSet(), edges.toSet())

		// Find the longest path between the starting and ending point
		return graph.findLongestPath(startingPoint, endingPoint)
	}

	private fun findLongestPath(
		start: Point,
		target: Point,
		neighbours: (Point) -> List<Point>,
	): BfsResult<Point> {
		// Queue of points and their paths to be checked
		val queue = ArrayDeque<Pair<Point, List<Point>>>()
		queue.add(start to emptyList())

		// The current maximum distances to reach any point
		val distances = mutableMapOf<Point, Int>().withDefault { 0 }

		// The current path with the maximum distance to reach any point
		val paths = mutableMapOf<Point, List<Point>>()

		while (queue.isNotEmpty()) {
			val (currentPoint, path) = queue.removeFirst()

			// Skip this queue entry if it is the target point
			if (currentPoint == target) continue

			neighbours.invoke(currentPoint)
				.filterNot { it in path } // Don't visit points twice
				.forEach { nextPoint ->
					// Check the current maximum distance for the next point and calculate the new distance from the current point
					val currentDistance = distances.getValue(nextPoint)
					val newDistance = distances.getValue(currentPoint) + 1

					// If the new distance is larger, remember it and add the next point to the queue
					if (newDistance > currentDistance) {
						distances[nextPoint] = newDistance

						val newPath = path.plus(nextPoint)
						queue.addFirst(nextPoint to newPath) // Add as first element in queue to check high-distance points first
						paths[nextPoint] = newPath
					}
				}
		}

		return BfsResult(start, target, paths.getValue(target), distances.getValue(target))
	}

	/**
	 * Result of the BFS algorithm, including the [path] to get from [start] to [target] and its [totalCost]
	 */
	private data class BfsResult<T>(val start: T, val target: T, val path: List<T>, val totalCost: Int)

	private data class Edge(val from: Point, val to: Point, val distance: Int)

	private class UndirectedGraph(vertices: Set<Point>, val edges: Set<Edge>) {

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

		fun findLongestPath(from: Point, target: Point): Int {
			// Find all paths, then return the one with the highest total distance
			val validPaths = findAllPaths(from, target, emptyMap(), Path(listOf(from), 0))
			return validPaths.maxOf { it.totalDistance }
		}

		fun findAllPaths(from: Point, target: Point, visited: Map<Point, Boolean>, currentPath: Path): List<Path> {
			// From and to are the same, return the current path
			if (from == target) return listOf(currentPath)

			// For each connected vertex, find a list of possible paths
			return adjacencies.getValue(from)
				.filterNot { (vertex, _) -> visited.getOrDefault(vertex, false) } // Filter out connected vertices that were already visited
				.map { (nextVertex, distance) ->
					// Find all paths from the next vertex to the target, marking the current one as visited and updating the current path
					findAllPaths(
						nextVertex,
						target,
						visited.set(from, true),
						Path(currentPath.vertices.plus(nextVertex), currentPath.totalDistance + distance)
					)
			}.flatten()
		}

		private data class Path(val vertices: List<Point>, val totalDistance: Int)
	}

	private fun printGrid(path: List<Point>, withSlopes: Boolean) {
		buildMap {
			putAll(forest.associateWith { '#' })
			if (withSlopes) {
				putAll(
					slopes.mapValues { (_, d) ->
						when (d) {
							Direction.NORTH -> '^'
							Direction.EAST -> '>'
							Direction.SOUTH -> 'v'
							Direction.WEST -> '<'
							else -> throw IllegalArgumentException("Not allowed direction $d")
						}
					}
				)
			}
			putAll(path.associateWith { 'O' })
			put(startingPoint, 'S')
		}.printAsCharGrid()
	}

}