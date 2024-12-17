package ch.mikewong.adventofcode.common.algorithms

import ch.mikewong.adventofcode.common.extensions.process
import java.util.*

/**
 * The result of a Dijkstra algorithm (shortest path in a weighted graph)
 *
 * @param start The starting node for the algorithm
 * @param target The target node of the algorithm
 * @param paths The shortest path(s) between [start] and [target]
 * @param totalCost The total cost of this path
 */
data class DijkstraResult<T>(val start: T, val target: T, val paths: List<List<T>>, val totalCost: Int)

/**
 * A generic Dijkstra algorithm, returning the shortest path from [startingNode] until [isTargetNode] returns true using the [costFunction] as weighting.
 *
 * @param T The type of node within this weighted graph
 * @param startingNode The starting node
 * @param startingCost The starting cost, defaults to zero
 * @param isTargetNode A lambda checking if the current [T] is the target node
 * @param neighbours A lambda returning the list of next nodes for a given node
 * @param costFunction A lambda returning the cost to go from the current node to the next node
 * @param findAllShortestPaths If true, the algorithm won't stop once the target node is reached, but continue to find all shortest paths.
 * 			This also means, that if this is true, it will visit the same node multiple times to find all paths with the shortest length
 */
fun <T> dijkstra(
	startingNode: T,
	startingCost: Int = 0,
	isTargetNode: (current: T) -> Boolean,
	neighbours: (current: T) -> List<T>,
	costFunction: (current: T, next: T) -> Int = { _, _ -> 1 },
	findAllShortestPaths: Boolean = false,
): DijkstraResult<T> {
	// Keep track which nodes were already visited
	val visitedNodes = mutableMapOf<T, Int>()

	// PriorityQueue of unvisited nodes and their current cost
	val queue = PriorityQueue<QueueEntry<T>>()
	queue.offer(QueueEntry(startingNode, startingCost, listOf(startingNode)))

	// Store the known minimum cost of the shortest path
	var minCost = Int.MAX_VALUE

	// Keep a list of all shortest paths to the target node
	val paths = mutableListOf<List<T>>()

	// Process the queue while there are entries left
	queue.process { (current, currentCost, path) ->
		if (findAllShortestPaths) {
			val bestKnownCost = visitedNodes.getOrDefault(current, Int.MAX_VALUE)
			if (currentCost < bestKnownCost) visitedNodes[current] = currentCost

			if (isTargetNode.invoke(current)) {
				when {
					// If the current cost is higher than the minimum cost, return all the paths
					currentCost > minCost -> return DijkstraResult(
						start = startingNode,
						target = current,
						paths = paths,
						totalCost = minCost
					)
					else -> {
						// Store the minimum cost and add the path to the list of paths
						minCost = currentCost
						paths.add(path)
					}
				}
			}

			neighbours.invoke(current).forEach { next ->
				// For each neighbouring node, add it to the visited nodes and calculate its cost
				if (currentCost <= bestKnownCost) {
					val nextCost = currentCost + costFunction.invoke(current, next)
					queue.offer(QueueEntry(next, nextCost, path + next))
				}
			}
		} else {
			if (isTargetNode.invoke(current)) {
				// If the target node is reached, calculate the final path and return it in the result
				return DijkstraResult(
					start = startingNode,
					target = current,
					paths = listOf(path),
					totalCost = currentCost
				)
			}

			neighbours.invoke(current).forEach { next ->
				// For each neighbouring node, add it to the visited nodes and calculate its cost
				if (!visitedNodes.containsKey(next)) {
					val nextCost = currentCost + costFunction.invoke(current, next)
					visitedNodes[next] = nextCost
					queue.offer(QueueEntry(next, nextCost, path + next))
				}
			}
		}
	}

	throw IllegalStateException("Target node never reached")
}

/** An intermediate queue entry for a [node] and its [currentCost] */
private data class QueueEntry<T>(val node: T, val currentCost: Int, val path: List<T>) : Comparable<QueueEntry<T>> {
	override fun compareTo(other: QueueEntry<T>) = this.currentCost.compareTo(other.currentCost)
}