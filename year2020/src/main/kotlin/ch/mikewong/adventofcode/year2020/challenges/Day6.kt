package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.getCharactersInCommon

class Day6 : Day<Int, Int>(2020, 6, "Custom Customs") {

	override fun partOne(): Int {
		return inputGroups.sumOf { it.joinToString("").toSet().count() }
	}

	override fun partTwo(): Int {
		return inputGroups.sumOf { it.getCharactersInCommon().count() }
	}

}