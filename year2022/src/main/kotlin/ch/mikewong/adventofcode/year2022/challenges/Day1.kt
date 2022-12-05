package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asInts

class Day1 : Day<Int, Int>(2022, 1, "Calorie Counting") {

	override fun partOne(): Int {
		return inputGroups.maxOf { it.asInts().sum() }
	}

	override fun partTwo(): Int {
		return inputGroups.map { it.asInts().sum() }.sortedDescending().take(3).sum()
	}
}