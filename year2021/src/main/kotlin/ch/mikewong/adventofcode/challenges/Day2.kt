package ch.mikewong.adventofcode.challenges

class Day2 : Day<Int, Int>(2, "Dive!") {

	private val input = inputLines.map {
		val parts = it.split(" ")
		Move(Direction.valueOf(parts.first().uppercase()), parts.last().toInt())
	}

	override fun partOne(): Int {
		var horizontalPosition = 0
		var verticalPosition = 0

		input.forEach {
			when (it.direction) {
				Direction.FORWARD -> horizontalPosition += it.distance
				Direction.UP -> verticalPosition -= it.distance
				Direction.DOWN -> verticalPosition += it.distance
			}
		}

		return horizontalPosition * verticalPosition
	}

	override fun partTwo(): Int {
		var aim = 0
		var horizontalPosition = 0
		var verticalPosition = 0

		input.forEach {
			when (it.direction) {
				Direction.FORWARD -> {
					horizontalPosition += it.distance
					verticalPosition += aim * it.distance
				}
				Direction.UP -> aim -= it.distance
				Direction.DOWN -> aim += it.distance
			}
		}

		return horizontalPosition * verticalPosition
	}

	private data class Move(val direction: Direction, val distance: Int)

	private enum class Direction {
		FORWARD, UP, DOWN
	}
}