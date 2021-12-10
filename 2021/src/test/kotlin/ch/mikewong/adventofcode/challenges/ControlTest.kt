package ch.mikewong.adventofcode.challenges

import org.junit.jupiter.api.Assertions.assertEquals
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
		val actualPartOne = answer.day.partOne()
		assertEquals(answer.expectedAnswerPartOne, actualPartOne, "Day ${answer.day.index} - Part 1 failed the control")

		val actualPartTwo = answer.day.partTwo()
		assertEquals(answer.expectedAnswerPartTwo, actualPartTwo, "Day ${answer.day.index} - Part 2 failed the control")
	}
}

private class DayProvider : ArgumentsProvider {
	override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
		return Stream.of(
			ControlAnswer(Day1(), 7, 5),
			ControlAnswer(Day2(), 150, 900),
			ControlAnswer(Day3(), 198, 230),
			ControlAnswer(Day4(), 4512, 1924),
			ControlAnswer(Day5(), 5, 12),
			ControlAnswer(Day6(), 5934L, 26984457539L),
			ControlAnswer(Day7(), 37, 168),
			ControlAnswer(Day8(), 26, 61229),
			ControlAnswer(Day9(), 15, 1134),
			ControlAnswer(Day10(), 26397, 288957),
		).map { Arguments.of(Named.of(it.day.toString(), it)) }
	}
}