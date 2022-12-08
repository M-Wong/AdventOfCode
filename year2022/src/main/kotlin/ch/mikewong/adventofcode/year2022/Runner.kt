package ch.mikewong.adventofcode.year2022

import ch.mikewong.adventofcode.common.BaseRunner
import ch.mikewong.adventofcode.year2022.challenges.Day1
import ch.mikewong.adventofcode.year2022.challenges.Day2
import ch.mikewong.adventofcode.year2022.challenges.Day3
import ch.mikewong.adventofcode.year2022.challenges.Day4
import ch.mikewong.adventofcode.year2022.challenges.Day5
import ch.mikewong.adventofcode.year2022.challenges.Day6
import ch.mikewong.adventofcode.year2022.challenges.Day7
import ch.mikewong.adventofcode.year2022.challenges.Day8

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
	)

	@JvmStatic
	fun main(args: Array<String>) = execute()
	
}