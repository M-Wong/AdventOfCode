package ch.mikewong.adventofcode.year2024.challenges

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
			ControlAnswer(Day1(), 11, 31),
			ControlAnswer(Day2(), 2, 4),
			ControlAnswer(Day3(), 161, 48),
			ControlAnswer(Day4(), 18, 9),
			ControlAnswer(Day5(), 143, 123),
			ControlAnswer(Day6(), 41, 6),
			ControlAnswer(Day7(), 3749, 11387),
			ControlAnswer(Day8(), 14, 34),
			ControlAnswer(Day9(), 1928, 2858),
			ControlAnswer(Day10(), 36, 81),
			ControlAnswer(Day11(), 55312, 65601038650482),
			ControlAnswer(Day12(), 1930, 1206),
			ControlAnswer(Day13(), 480, 875318608908),
		).map { Arguments.of(Named.of(it.day.toString(), it)) }
	}
}