package ch.mikewong.adventofcode.year2021

import ch.mikewong.adventofcode.common.BaseRunner
import ch.mikewong.adventofcode.year2021.challenges.Day1
import ch.mikewong.adventofcode.year2021.challenges.Day10
import ch.mikewong.adventofcode.year2021.challenges.Day11
import ch.mikewong.adventofcode.year2021.challenges.Day12
import ch.mikewong.adventofcode.year2021.challenges.Day13
import ch.mikewong.adventofcode.year2021.challenges.Day14
import ch.mikewong.adventofcode.year2021.challenges.Day15
import ch.mikewong.adventofcode.year2021.challenges.Day16
import ch.mikewong.adventofcode.year2021.challenges.Day17
import ch.mikewong.adventofcode.year2021.challenges.Day18
import ch.mikewong.adventofcode.year2021.challenges.Day19
import ch.mikewong.adventofcode.year2021.challenges.Day2
import ch.mikewong.adventofcode.year2021.challenges.Day20
import ch.mikewong.adventofcode.year2021.challenges.Day21
import ch.mikewong.adventofcode.year2021.challenges.Day22
import ch.mikewong.adventofcode.year2021.challenges.Day23
import ch.mikewong.adventofcode.year2021.challenges.Day24
import ch.mikewong.adventofcode.year2021.challenges.Day25
import ch.mikewong.adventofcode.year2021.challenges.Day3
import ch.mikewong.adventofcode.year2021.challenges.Day4
import ch.mikewong.adventofcode.year2021.challenges.Day5
import ch.mikewong.adventofcode.year2021.challenges.Day6
import ch.mikewong.adventofcode.year2021.challenges.Day7
import ch.mikewong.adventofcode.year2021.challenges.Day8
import ch.mikewong.adventofcode.year2021.challenges.Day9

object Runner : BaseRunner() {

	override val runMode = RunMode.AllDays
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
		Day25(),
	)

	@JvmStatic
	fun main(args: Array<String>) = execute()
}