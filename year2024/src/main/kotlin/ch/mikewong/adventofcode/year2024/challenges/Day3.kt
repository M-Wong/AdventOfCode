package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day3 : Day<Int, Int>(2024, 3, "Mull It Over") {

	private val multiplicationRegex = "(mul\\((?<first>\\d{1,3}),(?<second>\\d{1,3})\\))".toRegex()
	private val doRegex = "(do\\(\\))".toRegex()
	private val dontRegex = "(don't\\(\\))".toRegex()

	private val multiplicationMatches by lazy { multiplicationRegex.findAll(input).toList() }

	override fun partOne(): Int {
		return multiplicationMatches.sumMultiplications()
	}

	override fun partTwo(): Int {
		val dos = doRegex.findAll(input).map { it.range.last }.toList()
		val donts = dontRegex.findAll(input).map { it.range.last }.toList()

		return multiplicationMatches.filter { match ->
			val start = match.range.first

			// An enabled multiplication is one where the distance of its match to the last "do" is less than the distance to the last "don't"
			val doDiff = start - (dos.lastOrNull { it < start } ?: 0)
			val dontDiff = donts.lastOrNull { it < start }?.let { start - it } ?: Int.MAX_VALUE

			doDiff < dontDiff
		}.sumMultiplications()
	}

	private fun List<MatchResult>.sumMultiplications(): Int {
		return sumOf {  match ->
			val first = requireNotNull(match.groups["first"]?.value).toInt()
			val second = requireNotNull(match.groups["second"]?.value).toInt()
			first * second
		}
	}

}