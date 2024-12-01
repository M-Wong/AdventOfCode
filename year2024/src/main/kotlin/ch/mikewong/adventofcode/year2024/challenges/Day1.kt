package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import kotlin.math.abs

class Day1 : Day<Int, Int>(2024, 1, "Historian Hysteria") {

	private val leftSide by lazy { inputLines.map { it.split(" ").first().toInt() }.sorted() }
	private val rightSide by lazy { inputLines.map { it.split(" ").last().toInt() }.sorted() }

	override fun partOne(): Int {
		return leftSide.zip(rightSide).sumOf { (left, right) -> abs(left - right) }
	}

	override fun partTwo(): Int {
		return leftSide.sumOf { number -> rightSide.count { it == number } * number }
	}

}