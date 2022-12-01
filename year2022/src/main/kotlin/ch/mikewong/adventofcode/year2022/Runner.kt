package ch.mikewong.adventofcode.year2022

import ch.mikewong.adventofcode.common.BaseRunner
import ch.mikewong.adventofcode.year2022.challenges.Day1

object Runner : BaseRunner() {

	override val runMode = RunMode.SingleDay(1)
	override val days = listOf(
		Day1(),
	)

	@JvmStatic
	fun main(args: Array<String>) = execute()
	
}