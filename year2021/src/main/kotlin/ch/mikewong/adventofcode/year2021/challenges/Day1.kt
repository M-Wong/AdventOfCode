package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day1 : Day<Int, Int>(2021, 1, "Sonar Sweep") {

	private val input = inputLines.map { it.toInt() }

	override fun partOne(): Int {
		return input.zipWithNext()
			.count { it.second > it.first }
	}

	override fun partTwo(): Int {
		return input.asSequence()
			.windowed(3)
			.map { it.sum() }
			.zipWithNext()
			.count { it.second > it.first }
	}
}