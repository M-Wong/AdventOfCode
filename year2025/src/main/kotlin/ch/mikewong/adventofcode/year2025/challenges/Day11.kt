package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.models.DirectedGraph

class Day11 : Day<Long, Long>(2025, 11, "Reactor") {

	private val graphPartOne by lazy {
		// This day has two sample inputs but only one real input
		inputGroups.first().associate { line ->
			val (origin, destinationString) = line.split(": ")
			val destinations = destinationString.split(" ")
			origin to destinations.toSet()
		}.let { DirectedGraph(it) }
	}

	private val graphPartTwo by lazy {
		// This day has two sample inputs but only one real input
		inputGroups.last().associate { line ->
			val (origin, destinationString) = line.split(": ")
			val destinations = destinationString.split(" ")
			origin to destinations.toSet()
		}.let { DirectedGraph(it) }
	}

	/** 477 */
	override fun partOne(): Long {
		return graphPartOne.countPaths(start = "you", end = "out")
	}

	/** 383307150903216 */
	override fun partTwo(): Long {
		return graphPartTwo.countPaths(start = "svr", end = "out", mustVisit = setOf("dac", "fft"))
	}

}