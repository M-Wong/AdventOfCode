package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.fullyOverlapsWith
import ch.mikewong.adventofcode.common.extensions.overlapsWith

class Day4 : Day<Int, Int>(2022, 4, "Camp Cleanup") {

	private val assignments by lazy {
		inputLines.map { line ->
			val parts = line.split(",")
			val firstAssignment = Assignment.fromString(parts.first())
			val secondAssignment = Assignment.fromString(parts.last())
			firstAssignment to secondAssignment
		}
	}

	override fun partOne(): Int {
		return assignments.count { (a, b) ->
			a.range.fullyOverlapsWith(b.range)
		}
	}

	override fun partTwo(): Int {
		return assignments.count { (a, b) ->
			a.range.overlapsWith(b.range)
		}
	}

	private data class Assignment(private val start: Int, private val end: Int) {
		companion object {
			fun fromString(s: String) = with(s.split("-")) { Assignment(first().toInt(), last().toInt()) }
		}

		val range = start..end
	}

}