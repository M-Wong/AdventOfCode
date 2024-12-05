package ch.mikewong.adventofcode.common.models

class UndirectedGraph<T> {

	private val graph = mutableMapOf<T, Set<T>>().withDefault { emptySet() }

	/**
	 * Constructs an undirected graph from a list of pairs, where each pair is an edge in the graph connecting [Pair.first] to [Pair.second] and vice versa
	 */
	constructor(paths: List<Pair<T, T>>) {
		paths.forEach { (start, end) ->
			graph[start] = graph.getValue(start) + end
			graph[end] = graph.getValue(end) + start
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
	 * @return A new graph with all edges removed from and to [node]
	 */
	fun removeNode(node: T): UndirectedGraph<T> {
		val graphMinusStartNode = graph.filter { it.key != node }
			.mapValues { (_, v) -> v - node }
		return UndirectedGraph(graphMinusStartNode)
	}

}