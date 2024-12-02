package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asInts

class Day2 : Day<Int, Int>(2024, 2, "Red-Nosed Reports") {

	private val reports by lazy {
		inputLines.map { it.split(" ").asInts() }
	}

	override fun partOne(): Int {
		val reportDiffs = reports.map { it.toDiffs() }
		return reportDiffs.count { it.isSafe() }
	}

	override fun partTwo(): Int {
		val allReportDiffs = reports.map { report ->
			// Basically create a list of all possible reports by removing every level once from the report
			report.indices.map { i -> report.toMutableList().also { it.removeAt(i) }.toDiffs() }
		}
		return allReportDiffs.count { reportDiffs ->
			reportDiffs.any { it.isSafe() }
		}
	}

	private fun List<Int>.toDiffs() = this.zipWithNext().map { (a, b) -> b - a }

	private fun List<Int>.isSafe() = this.all { it in 1..3 } || this.all { it in -3..-1 }

}