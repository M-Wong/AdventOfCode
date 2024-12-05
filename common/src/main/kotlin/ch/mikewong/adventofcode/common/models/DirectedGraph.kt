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
	 * @return A new graph with all edges removed from and to [node]
	 */
	fun removeNode(node: T): DirectedGraph<T> {
		val graphMinusStartNode = graph.filter { it.key != node }
			.mapValues { (_, v) -> v - node }
		return DirectedGraph(graphMinusStartNode)
	}

	/**
	 * @return A subgraph where only the edges between [nodes] are included
	 */
	fun subGraph(nodes: Set<T>): DirectedGraph<T> {
		val subGraph = graph.filterKeys { it in nodes }
			.mapValues { (_, v) -> v.filter { it in nodes }.toSet() }
		return DirectedGraph(subGraph)
	}

}