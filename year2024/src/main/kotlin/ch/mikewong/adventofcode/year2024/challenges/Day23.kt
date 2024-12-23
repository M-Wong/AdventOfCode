package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.UndirectedGraph

class Day23 : Day<Int, String>(2024, 23, "LAN Party") {

	private val networkGraph by lazy { readInput() }

	// 1358
	override fun partOne(): Int {
		val allComputers = networkGraph.getAllNodes()
		val relevantComputers = allComputers.filter { it.startsWith("t") }

		val triangles = mutableSetOf<Set<String>>()

		relevantComputers.forEach { computer ->
			// Get all nodes this computer is connected to
			val connected = networkGraph.getConnectingNodes(computer)

			connected.forEach { first ->
				// Find all common neighbours between the computer and this connected node
				val commonNodes = connected.intersect(networkGraph.getConnectingNodes(first))

				// If common neighbours is not empty, we found a triangle
				commonNodes.forEach { second ->
					triangles.add(setOf(computer, first, second))
				}
			}
		}

		return triangles.size
	}

	// cl,ei,fd,hc,ib,kq,kv,ky,rv,vf,wk,yx,zf
	override fun partTwo(): String {
		val maximalCliques = networkGraph.maximalCliques()
		return maximalCliques.single().sorted().joinToString(",")
	}

	private fun readInput(): UndirectedGraph<String> {
		val edges = inputLines.map { line ->
			val (start, end) = line.split("-")
			start to end
		}
		return UndirectedGraph(edges)
	}

}