package ch.mikewong.adventofcode.year2022

import ch.mikewong.adventofcode.common.BaseRunner
import ch.mikewong.adventofcode.year2022.challenges.*

object Runner : BaseRunner() {

	override val runMode = RunMode.LastDay
	override val days = listOf(
		Day1(),
		Day2(),
		Day3(),
		Day4(),
		Day5(),
		Day6(),
		Day7(),
		Day8(),
		Day9(),
		Day10(),
		Day11(),
	)

	@JvmStatic
	fun main(args: Array<String>) = execute()
	
}