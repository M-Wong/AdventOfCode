package ch.mikewong.adventofcode.year2020

import ch.mikewong.adventofcode.common.BaseRunner
import ch.mikewong.adventofcode.year2020.challenges.Day1

object Runner : BaseRunner() {

	override val runMode = RunMode.LastDay
	override val days = listOf(
		Day1(),
	)

	@JvmStatic
	fun main(args: Array<String>) = execute()

}