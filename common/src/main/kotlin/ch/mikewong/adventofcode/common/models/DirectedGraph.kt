package ch.mikewong.adventofcode.common.models

class DirectedGraph<T> {

	private val graph = mutableMapOf<T, Set<T>>().withDefault { emptySet() }

	/**
	 * Constructs a directed graph from a list of pairs, where each pair is an edge in the graph connecting [Pair.first] to [Pair.second]
	 */
	constructor(paths: List<Pair<T, T>>) {
		paths.forEach { (start, end) ->
			graph[start] = graph.getValue(start) + end
		}
	}

	constructor(graph: Map<T, Set<T>>) {
		this.graph.putAll(graph)
	}

	/**
	 * @return All nodes that [node] connects to
	 */
	fun getConnectingNodes(node: T) = graph.getValue(node)

	/**
	 * @return A subgraph where only the edges between [nodes] are included
	 */
	fun subGraph(nodes: Set<T>): DirectedGraph<T> {
		val subGraph = graph.filterKeys { it in nodes }
			.mapValues { (_, v) -> v.filter { it in nodes }.toSet() }
		return DirectedGraph(subGraph)
	}

	/**
	 * Counts all paths from [start] to [end] with an optional set of [mustVisit] nodes
	 * This does not return the actual paths, so it is more performant for large search spaces
	 */
	fun countPaths(start: T, end: T, mustVisit: Set<T> = emptySet()): Long {
		val pathCounts = mutableMapOf<String, Long>()

		fun recurse(node: T, leftToVisit: Set<T>): Long {
			if (node == end) return if (leftToVisit.isEmpty()) 1 else 0

			val key = "$node-$leftToVisit"
			pathCounts[key]?.let { return it }

			return getConnectingNodes(node)
				.sumOf { next -> recurse(next, leftToVisit - next) }
				.also { pathCounts[key] = it }
		}

		return recurse(start, mustVisit)
	}

	/**
	 * Finds all paths from [start] to [end]
	 * This does return the actual paths, so it is less performant for
	 */
	fun findPaths(start: T, end: T): List<Path<T>> {
		val paths = mutableListOf<Path<T>>()
		val queue = ArrayDeque<Path<T>>()

		getConnectingNodes(start).forEach {
			queue.add(Path(listOf(start, it)))
		}

		while (queue.isNotEmpty()) {
			val currentPath = queue.removeFirst()
			val currentNode = currentPath.nodes.last()
			val previousNodes = currentPath.nodes.dropLast(1)

			if (currentNode == end) {
				paths.add(currentPath)
			} else {
				val nextNodes = getConnectingNodes(currentNode)
				nextNodes.forEach { node ->
					if (node !in previousNodes) {
						queue.add(Path(currentPath.nodes + node))
					}
				}
			}
		}

		return paths
	}

	data class Path<T>(val nodes: List<T>)

}