package ch.mikewong.adventofcode

import ch.mikewong.adventofcode.challenges.Day
import ch.mikewong.adventofcode.challenges.Day1
import ch.mikewong.adventofcode.challenges.Day2
import ch.mikewong.adventofcode.challenges.Day3
import ch.mikewong.adventofcode.challenges.Day4
import ch.mikewong.adventofcode.challenges.Day5
import ch.mikewong.adventofcode.challenges.Day6
import ch.mikewong.adventofcode.challenges.Day7

object Application {

	private val runMode: RunMode = RunMode.SingleDay(7)
	private val days: List<Day<*, *>> = listOf(
		Day1(),
		Day2(),
		Day3(),
		Day4(),
		Day5(),
		Day6(),
		Day7(),
	)

	@JvmStatic
	fun main(args: Array<String>) {
		when (runMode) {
			is RunMode.AllDays -> days.forEach {
				runSingleDay(it)
				println()
			}
			is RunMode.SingleDay -> runSingleDay(days[runMode.day - 1])
		}
	}

	private fun runSingleDay(day: Day<*, *>) {
		println("--- Day ${day.index}: ${day.title} ---")
		val partOneResult = day.partOne()
		println("Part 1: $partOneResult")

		val partTwoResult = day.partTwo()
		println("Part 2: $partTwoResult")
	}

	sealed class RunMode {
		object AllDays : RunMode()
		data class SingleDay(val day: Int) : RunMode()
	}
}