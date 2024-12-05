package ch.mikewong.adventofcode.year2024

import ch.mikewong.adventofcode.common.BaseRunner
import ch.mikewong.adventofcode.year2024.challenges.*

object Runner : BaseRunner() {

	override val runMode = RunMode.LastDay
	override val days = listOf(
		Day1(),
		Day2(),
		Day3(),
		Day4(),
		Day5(),
	)

	@JvmStatic
	fun main(args: Array<String>) = execute()

}