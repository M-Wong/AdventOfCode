package ch.mikewong.adventofcode.challenges

class Day1 : Day<Int, Int>(1, "Sonar Sweep") {

	private val input = rawInput.map { it.toInt() }

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