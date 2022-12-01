package ch.mikewong.adventofcode.common.models

class Graph {

	private val graph = mutableMapOf<String, Set<String>>().withDefault { emptySet() }

	constructor(paths: List<Pair<String, String>>) {
		paths.forEach { (start, end) ->
			graph[start] = graph.getValue(start) + end
			graph[end] = graph.getValue(end) + start
		}
	}

	constructor(graph: Map<String, Set<String>>) {
		this.graph.putAll(graph)
	}

	fun getConnectingNodes(node: String) = graph.getValue(node)

	fun filterNode(node: String): Graph {
		val graphMinusStartNode = graph.filter { it.key != node }.mapValues { (_, v) -> v - node }
		return Graph(graphMinusStartNode)
	}

}