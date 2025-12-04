package ch.mikewong.adventofcode.year2025.challenges

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
			ControlAnswer(Day1(), 3, 6),
			ControlAnswer(Day2(), 1227775554, 4174379265),
			ControlAnswer(Day3(), 357, 3121910778619),
			ControlAnswer(Day4(), 13, 43),
		).map { Arguments.of(Named.of(it.day.toString(), it)) }
	}
}