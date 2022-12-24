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
		Day12(),
		Day13(),
		Day14(),
		Day15(),
		Day16(),
		Day17(),
		Day18(),
		Day19(),
		Day20(),
		Day21(),
		Day22(),
		Day23(),
		Day24(),
	)

	@JvmStatic
	fun main(args: Array<String>) = execute()
	
}