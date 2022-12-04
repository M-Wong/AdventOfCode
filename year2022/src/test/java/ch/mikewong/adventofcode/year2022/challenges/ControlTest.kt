package ch.mikewong.adventofcode.year2022.challenges

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
		val actualPartOne = answer.day.partOne()
		Assertions.assertEquals(answer.expectedAnswerPartOne, actualPartOne, "Day ${answer.day.index} - Part 1 failed the control")

		val actualPartTwo = answer.day.partTwo()
		Assertions.assertEquals(answer.expectedAnswerPartTwo, actualPartTwo, "Day ${answer.day.index} - Part 2 failed the control")
	}
}

private class DayProvider : ArgumentsProvider {
	override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> {
		return Stream.of(
			ControlAnswer(Day1(), 24000, 45000),
			ControlAnswer(Day2(), 15, 12),
			ControlAnswer(Day3(), 157, 70),
			ControlAnswer(Day4(), 2, 4),
		).map { Arguments.of(Named.of(it.day.toString(), it)) }
	}
}