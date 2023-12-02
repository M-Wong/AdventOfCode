package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asInts

class Day1 : Day<Int, Int>(2020, 1, "Report Repair") {

	private val expenseReport = inputLines.asInts()

	override fun partOne(): Int {
		return expenseReport.firstNotNullOf { a ->
			// Iterate all numbers and find the first one that equals 2020 minus the first number
			expenseReport.firstOrNull { it == 2020 - a }?.let { a * it }
		}
	}

	override fun partTwo(): Int {
		return expenseReport.firstNotNullOf { a ->
			expenseReport.firstNotNullOfOrNull { b ->
				expenseReport.firstOrNull { it == 2020 - a - b }?.let { a * b * it }
			}
		}
	}

}