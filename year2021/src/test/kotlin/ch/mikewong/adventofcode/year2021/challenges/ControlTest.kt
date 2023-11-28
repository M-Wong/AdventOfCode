package ch.mikewong.adventofcode.year2021.challenges

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
			ControlAnswer(Day11(), 1656, 195),
			ControlAnswer(Day12(), 10, 36),
			ControlAnswer(Day13(), 17, "UFRZKAUZ"),
			ControlAnswer(Day14(), 1588L, 2188189693529L),
			ControlAnswer(Day15(), 40, 315),
			ControlAnswer(Day16(), 14, 3),
			ControlAnswer(Day17(), 45, 112),
			ControlAnswer(Day18(), 4140, 3993),
			ControlAnswer(Day19(), 79, 3621),
			ControlAnswer(Day20(), 35, 3351),
			ControlAnswer(Day21(), 739785, 444356092776315L),
			ControlAnswer(Day22(), 474140, 2758514936282235L),
			ControlAnswer(Day23(), 12521, 44169),
			ControlAnswer(Day24(), 36969794979199L, 11419161313147L),
			ControlAnswer(Day25(), 58, 0),
		).map { Arguments.of(Named.of(it.day.toString(), it)) }
	}
}