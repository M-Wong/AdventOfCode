package ch.mikewong.adventofcode.common.test

import ch.mikewong.adventofcode.common.challenges.Day

class ControlAnswer<O, T>(
	val day: Day<O, T>,
	val expectedAnswerPartOne: O,
	val expectedAnswerPartTwo: T,
)