package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.util.InputUtil

abstract class Day<O, T>(val index: Int, val title: String) {

	protected val rawInput: List<String> by lazy { InputUtil.readInputLines(index) }
	protected val groupedInput: List<List<String>> by lazy { InputUtil.readInputGroups(index) }

	abstract fun partOne(): O
	abstract fun partTwo(): T
}