package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asInts

class Day1 : Day<Int, Int>(2021, 1, "Sonar Sweep") {

	private val intLines = inputLines.asInts()

	override fun partOne(): Int {
		return intLines.zipWithNext()
			.count { it.second > it.first }
	}

	override fun partTwo(): Int {
		return intLines.asSequence()
			.windowed(3)
			.map { it.sum() }
			.zipWithNext()
			.count { it.second > it.first }
	}
}