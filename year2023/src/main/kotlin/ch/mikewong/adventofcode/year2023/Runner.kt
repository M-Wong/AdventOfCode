package ch.mikewong.adventofcode.year2023

import ch.mikewong.adventofcode.common.BaseRunner
import ch.mikewong.adventofcode.year2023.challenges.Day1

object Runner : BaseRunner() {

	override val runMode = RunMode.AllDays
	override val days = listOf(
		Day1(),
	)

	@JvmStatic
	fun main(args: Array<String>) = execute()

}