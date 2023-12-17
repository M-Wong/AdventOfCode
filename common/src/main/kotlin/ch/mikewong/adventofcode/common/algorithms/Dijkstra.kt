package ch.mikewong.adventofcode.common.algorithms

import ch.mikewong.adventofcode.common.extensions.process
import java.util.*

/**
 * The result of a Dijkstra algorithm (shortest path in a weighted graph)
 *
 * @param start The starting node for the algorithm
 * @param target The target node of the algorithm
 * @param path The shortest path between [start] and [target]
 * @param totalCost The total cost of this path
 */
data class DijkstraResult<T>(val start: T, val target: T, val path: List<T>, val totalCost: Int)

/**
 * A generic Dijkstra algorithm, returning the shortest path from [startingNode] until [isTargetNode] returns true using the [costFunction] as weighting.
 *
 * @param T The type of node within this weighted graph
 * @param startingNode The starting node
 * @param startingCost The starting cost, defaults to zero
 * @param isTargetNode A lambda checking if the current [T] is the target node
 * @param neighbours A lambda returning the list of next nodes for a given node
 * @param costFunction A lambda returning the cost to go from the current node to the next node
 */
fun <T> dijkstra(
	startingNode: T,
	startingCost: Int = 0,
	isTargetNode: (current: T) -> Boolean,
	neighbours: (current: T) -> List<T>,
	costFunction: (current: T, next: T) -> Int = { _, _ -> 1 },
): DijkstraResult<T> {
	// Keep track which nodes were already visited
	val visitedNodes = mutableSetOf(startingNode)

	// Keep track which node leads to which
	val previousNodes = mutableMapOf<T, T>()

	// PriorityQueue of unvisited nodes and their current cost
	val queue = PriorityQueue<QueueEntry<T>>()
	queue.offer(QueueEntry(startingNode, startingCost))

	// Process the queue while there are entries left
	queue.process { (current, currentCost) ->
		if (isTargetNode.invoke(current)) {
			// If the target node is reached, calculate the final path and return it in the result
			return DijkstraResult(
				start = startingNode,
				target = current,
				path = generateSequence(current) { previousNodes[it] }.toList().reversed(),
				totalCost = currentCost
			)
		}

		neighbours.invoke(current).forEach { next ->
			// For each neighbouring node, add it to the visited nodes and calculate its cost
			if (visitedNodes.add(next)) {
				previousNodes[next] = current
				val nextCost = currentCost + costFunction.invoke(current, next)
				queue.offer(QueueEntry(next, nextCost))
			}
		}
	}

	throw IllegalStateException("Target node never reached")
}

/** An intermediate queue entry for a [node] and its [currentCost] */
private data class QueueEntry<T>(val node: T, val currentCost: Int) : Comparable<QueueEntry<T>> {
	override fun compareTo(other: QueueEntry<T>) = this.currentCost.compareTo(other.currentCost)
}