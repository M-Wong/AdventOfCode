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
//			ControlAnswer(Day1(), 24000, 45000),
//			ControlAnswer(Day2(), 15, 12),
//			ControlAnswer(Day3(), 157, 70),
//			ControlAnswer(Day4(), 2, 4),
//			ControlAnswer(Day5(), "CMZ", "MCD"),
//			ControlAnswer(Day6(), 7, 19),
//			ControlAnswer(Day7(), 95437, 24933642),
//			ControlAnswer(Day8(), 21, 8),
//			ControlAnswer(Day9(), 88, 36),
//			ControlAnswer(Day10(), 13140, "RZHFGJCB"),
//			ControlAnswer(Day11(), 10605L, 2713310158L),
//			ControlAnswer(Day12(), 31, 29),
//			ControlAnswer(Day13(), 13, 140),
//			ControlAnswer(Day14(), 24, 93),
			ControlAnswer(Day15(), 26, 56000011L),
		).map { Arguments.of(Named.of(it.day.toString(), it)) }
	}
}