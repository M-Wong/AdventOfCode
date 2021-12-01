package ch.mikewong.adventofcode.challenges

class Day1 : Day(1) {

	private val input = rawInput.map { it.toInt() }

	override fun partOne(): Any {
		return input.zipWithNext()
			.count { it.second > it.first }
	}

	override fun partTwo(): Any {
		return input.asSequence()
			.windowed(3)
			.map { it.sum() }
			.zipWithNext()
			.count { it.second > it.first }
	}
}