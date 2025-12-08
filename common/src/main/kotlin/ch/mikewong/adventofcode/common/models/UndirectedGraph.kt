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
	 * @return All nodes in the graph
	 */
	fun getAllNodes() = graph.keys

	/**
	 * @return All nodes that [node] connects to
	 */
	fun getConnectingNodes(node: T) = graph.getValue(node)

	/**
	 * @return True if there is an edge between [first] and [second]
	 */
	fun areConnected(first: T, second: T) = graph.getValue(first).contains(second)

	/**
	 * @return A new graph with all edges removed from and to [node]
	 */
	fun removeNode(node: T): UndirectedGraph<T> {
		val graphMinusStartNode = graph.filter { it.key != node }
			.mapValues { (_, v) -> v - node }
		return UndirectedGraph(graphMinusStartNode)
	}

	/**
	 * Bron-Kerbosch algorithm to find all maximal cliques (set of nodes that are all connected to each other) within the graph
	 * This implements the Non-Pivot version of the algorithm, which might be less efficient but is easier to understand/implement
	 */
	fun maximalCliques(): Set<Set<T>> {
		val allNodes = graph.keys
		val largestCliques = mutableSetOf<Set<T>>()
		var maxSize = 0

		fun bronKerbosch(
			current: Set<T>, // Current clique (r)
			potential: Set<T>, // Potential nodes to add to the clique (p)
			exclude: Set<T>, // Nodes that should be excluded (x)
		) {
			if (potential.isEmpty() && exclude.isEmpty()) {
				// Found a maximal clique
				when {
					current.size > maxSize -> {
						// Found a clique of larger size, reset the list
						largestCliques.clear()
						largestCliques.add(current)
						maxSize = current.size
					}
					current.size == maxSize -> {
						// Found another clique of the same maximal size
						largestCliques.add(current)
					}
				}
				return
			} else {
				val newPotential = potential.toMutableSet()
				val newExclude = exclude.toMutableSet()

				// For each potential node in the clique, recursively add it to the clique and update the new potential and exclude nodes
				while (newPotential.isNotEmpty()) {
					val node = newPotential.first().also { newPotential.remove(it) }
					val neighbors = graph.getValue(node)

					bronKerbosch(
						current + node,
						newPotential.intersect(neighbors),
						newExclude.intersect(neighbors)
					)

					// Move the node from the potential to the exclude set
					newPotential -= node
					newExclude += node
				}
			}
		}

		bronKerbosch(emptySet(), allNodes.toSet(), emptySet())
		return largestCliques
	}

	/**
	 * Returns a set of all connected nodes in the graph.
	 * If all nodes are connected, this will return a set with a single set containing all nodes.
	 */
	fun connectedComponents(): Set<Set<T>> {
		val visited = mutableSetOf<T>()
		val components = mutableSetOf<Set<T>>()

		// DFS to find all nodes connected to the current node
		fun findComponent(currentNode: T): Set<T> {
			val component = mutableSetOf<T>()
			val toVisit = mutableListOf(currentNode)

			while (toVisit.isNotEmpty()) {
				val node = toVisit.removeAt(toVisit.size - 1)
				if (node !in component) {
					component.add(node)
					val neighbors = getConnectingNodes(node)
					neighbors.forEach { toVisit.add(it) }
				}
			}

			return component
		}

		getAllNodes().forEach { node ->
			if (node !in visited) {
				visited.add(node)
				components.add(findComponent(node))
			}
		}

		return components
	}

}