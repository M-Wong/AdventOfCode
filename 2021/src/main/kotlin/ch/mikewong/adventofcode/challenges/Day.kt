package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.util.InputUtil

abstract class Day<O, T>(val index: Int, val title: String) {
	val rawInput = InputUtil.readInputLines(index)
	abstract fun partOne(): O
	abstract fun partTwo(): T
}