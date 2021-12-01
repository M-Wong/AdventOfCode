package ch.mikewong.adventofcode

import ch.mikewong.adventofcode.challenges.Day
import ch.mikewong.adventofcode.challenges.Day1

object Application {

	private val mode: Mode = Mode.AllDays
	private val days: List<Day> = listOf(
		Day1()
	)

	@JvmStatic
	fun main(args: Array<String>) {
		when (mode) {
			is Mode.AllDays -> days.forEach {
				runSingleDay(it)
				println()
			}
			is Mode.SingleDay -> runSingleDay(days[mode.day])
		}
	}

	private fun runSingleDay(day: Day) {
		println("--- Day ${day.index} ---")
		val partOneResult = day.partOne()
		println("Part 1: $partOneResult")

		val partTwoResult = day.partTwo()
		println("Part 2: $partTwoResult")
	}

	sealed class Mode {
		object AllDays : Mode()
		data class SingleDay(val day: Int) : Mode()
	}
}