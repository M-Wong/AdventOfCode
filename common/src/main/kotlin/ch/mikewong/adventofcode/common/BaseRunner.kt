package ch.mikewong.adventofcode.common

import ch.mikewong.adventofcode.common.challenges.Day
import kotlin.system.measureNanoTime

abstract class BaseRunner {

	abstract val runMode: RunMode
	abstract val days: List<Day<*, *>>

	protected fun execute() {
		val time = measureNanoTime {
			when (val mode = runMode) {
				is RunMode.AllDays -> days.forEach {
					runSingleDay(it)
					println()
				}

				is RunMode.SingleDay -> runSingleDay(days[mode.day - 1])
				is RunMode.LastDay -> runSingleDay(days.last())
			}
		} / 1_000_000f
		println("Execution took $time ms in total")
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
		object LastDay : RunMode()
	}
}