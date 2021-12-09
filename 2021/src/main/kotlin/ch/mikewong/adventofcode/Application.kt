package ch.mikewong.adventofcode

import ch.mikewong.adventofcode.challenges.Day
import ch.mikewong.adventofcode.challenges.Day1
import ch.mikewong.adventofcode.challenges.Day2
import ch.mikewong.adventofcode.challenges.Day3
import ch.mikewong.adventofcode.challenges.Day4
import ch.mikewong.adventofcode.challenges.Day5
import ch.mikewong.adventofcode.challenges.Day6
import ch.mikewong.adventofcode.challenges.Day7
import ch.mikewong.adventofcode.challenges.Day8
import ch.mikewong.adventofcode.challenges.Day9
import kotlin.system.measureNanoTime

object Application {

	private val runMode: RunMode = RunMode.SingleDay(9)
	private val days: List<Day<*, *>> = listOf(
		Day1(),
		Day2(),
		Day3(),
		Day4(),
		Day5(),
		Day6(),
		Day7(),
		Day8(),
		Day9(),
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
		runAndMeasurePart(1) { day.partOne() }
		runAndMeasurePart(2) { day.partTwo() }
	}

	private fun runAndMeasurePart(part: Int, block: () -> Any?) {
		val result: Any?
		val time = measureNanoTime {
			result = block.invoke()
		} / 1_000_000f
		println("Part $part: $result (took $time ms)")

	}

	sealed class RunMode {
		object AllDays : RunMode()
		data class SingleDay(val day: Int) : RunMode()
	}
}