package ch.mikewong.adventofcode.year2022

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.year2022.challenges.Day1
import kotlin.system.measureNanoTime

object Runner {

	private val runMode: RunMode = RunMode.SingleDay(1)
	private val days: List<Day<*, *>> = listOf(
		Day1(),
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