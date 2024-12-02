package ch.mikewong.adventofcode.year2024

import ch.mikewong.adventofcode.common.BaseRunner
import ch.mikewong.adventofcode.year2024.challenges.Day1
import ch.mikewong.adventofcode.year2024.challenges.Day2

object Runner : BaseRunner() {

	override val runMode = RunMode.LastDay
	override val days = listOf(
		Day1(),
		Day2(),
	)

	@JvmStatic
	fun main(args: Array<String>) = execute()

}