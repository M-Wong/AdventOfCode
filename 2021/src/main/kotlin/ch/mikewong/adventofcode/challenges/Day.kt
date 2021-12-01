package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.util.InputUtil

abstract class Day(val index: Int) {
	val rawInput = InputUtil.readInputLines(index)
	abstract fun partOne(): Any
	abstract fun partTwo(): Any
}