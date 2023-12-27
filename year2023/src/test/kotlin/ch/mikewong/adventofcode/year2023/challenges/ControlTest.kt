package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.test.ControlAnswer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Named
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class ControlTest {

	@ParameterizedTest
	@ArgumentsSource(DayProvider::class)
	fun controlAnswers(answer: ControlAnswer<*, *>) {
		answer.day.isControlSet = true

		val actualPartOne = answer.day.partOne()
		Assertions.assertEquals(answer.expectedAnswerPartOne, actualPartOne, "Day ${answer.day.day} - Part 1 failed the control")
		println("Day ${answer.day} - Part 1 passed the control ($actualPartOne == ${answer.expectedAnswerPartOne})")

		val actualPartTwo = answer.day.partTwo()
		Assertions.assertEquals(answer.expectedAnswerPartTwo, actualPartTwo, "Day ${answer.day.day} - Part 2 failed the control")
		println("Day ${answer.day} - Part 2 passed the control ($actualPartTwo == ${answer.expectedAnswerPartTwo})")
	}
}

private class DayProvider : ArgumentsProvider {
	override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
		return Stream.of(
			ControlAnswer(Day1(), 209, 281),
			ControlAnswer(Day2(), 8, 2286),
			ControlAnswer(Day3(), 4361, 467835),
			ControlAnswer(Day4(), 13, 30),
			ControlAnswer(Day5(), 35L, 46L),
			ControlAnswer(Day6(), 288, 71503),
			ControlAnswer(Day7(), 6440, 5905),
			ControlAnswer(Day8(), 2, 6L),
			ControlAnswer(Day9(), 114, 2),
			ControlAnswer(Day10(), 23, 4),
			ControlAnswer(Day11(), 374, 8410),
			ControlAnswer(Day12(), 21, 525152L),
			ControlAnswer(Day13(), 405, 400),
			ControlAnswer(Day14(), 136, 64),
			ControlAnswer(Day15(), 1320, 145),
			ControlAnswer(Day16(), 46, 51),
			ControlAnswer(Day17(), 102, 94),
			ControlAnswer(Day18(), 62, 952408144115L),
			ControlAnswer(Day19(), 19114, 167409079868000L),
			ControlAnswer(Day20(), 32000000, 0),
			ControlAnswer(Day21(), 16, 6536),
			ControlAnswer(Day22(), 5, 7),
			ControlAnswer(Day23(), 94, 154),
			ControlAnswer(Day24(), 2, 47),
			ControlAnswer(Day25(), 54, 0),
		).map { Arguments.of(Named.of(it.day.toString(), it)) }
	}
}