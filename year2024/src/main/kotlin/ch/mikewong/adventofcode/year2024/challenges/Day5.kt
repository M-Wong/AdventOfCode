package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asInts
import ch.mikewong.adventofcode.common.extensions.middle
import ch.mikewong.adventofcode.common.models.DirectedGraph

class Day5 : Day<Int, Int>(2024, 5, "Print Queue") {

	private val pageOrderRules by lazy {
		inputGroups.first().map { line ->
			val (from, to) = line.split("|")
			from.toInt() to to.toInt()
		}.let { DirectedGraph(it) }
	}

	private val updates by lazy {
		inputGroups.last().map { line ->
			line.split(",").asInts()
		}
	}

	override fun partOne(): Int {
		return updates.filter { isValidUpdate(it) }
			.sumOf { it.middle() }
	}

	override fun partTwo(): Int {
		val invalidUpdates = updates.filter { !isValidUpdate(it) }
		return invalidUpdates.map { sortUpdate(it) }
			.sumOf { it.middle() }
	}

	private fun isValidUpdate(update: List<Int>): Boolean {
		if (update.isEmpty()) return true

		val first = update.first()
		val rest = update.drop(1)
		rest.forEach { other ->
			val rules = pageOrderRules.getConnectingNodes(other)
			if (first in rules) return false
		}
		return isValidUpdate(rest)
	}

	private fun sortUpdate(update: List<Int>): List<Int> {
		val subGraph = pageOrderRules.subGraph(update.toSet())

		return update.sortedByDescending { subGraph.getConnectingNodes(it).size }
	}

}