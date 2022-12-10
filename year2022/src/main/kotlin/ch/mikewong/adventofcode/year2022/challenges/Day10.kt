package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day10 : Day<Int, String>(2022, 10, "Cathode-Ray Tube") {

	override fun partOne(): Int {
		var currentCycleCount = 1
		var registerValue = 1
		var totalSignalStrength = 0
		val interestingCycles = listOf(20, 60, 100, 140, 180, 220)

		inputLines.forEach {  line ->
			currentCycleCount++

			when (val instruction = Instruction.fromString(line)) {
				is Instruction.Noop -> {} // NOOP
				is Instruction.AddX -> {
					// Check the signal strength in the middle of the add instruction
					if (currentCycleCount in interestingCycles) {
						totalSignalStrength += currentCycleCount * registerValue
					}

					currentCycleCount++
					registerValue += instruction.value
				}
			}

			// Check the signal strength at the end of the instruction
			if (currentCycleCount in interestingCycles) {
				totalSignalStrength += currentCycleCount * registerValue
			}
		}

		return totalSignalStrength
	}

	override fun partTwo(): String {
		val crt = Array(6) { Array(40) { "." } }

		var currentCycleCount = 1
		var registerValue = 1

		crt[0][0] = "#"

		inputLines.forEach { line ->
			when (val instruction = Instruction.fromString(line)) {
				is Instruction.Noop -> {} // NOOP
				is Instruction.AddX -> {
					// Check if the current register value should light up the CRT in the middle of the add instruction
					if (currentCycleCount % 40 in (registerValue - 1 .. registerValue + 1)) {
						val row = currentCycleCount / 40
						val col = currentCycleCount % 40
						crt[row][col] = "#"
					}
					currentCycleCount++

					registerValue += instruction.value
				}
			}

			// Check if the current register value should light up the CRT at the end of the instruction
			if (currentCycleCount % 40 in (registerValue - 1 .. registerValue + 1)) {
				val row = currentCycleCount / 40
				val col = currentCycleCount % 40
				crt[row][col] = "#"
			}

			currentCycleCount++
		}


		crt.forEach { line ->
			println(line.joinToString(""))
		}
		return "RZHFGJCB"
	}

	private sealed class Instruction {
		companion object {
			fun fromString(s: String) = when {
				s == "noop" -> Noop
				s.startsWith("addx") -> AddX(s.substringAfter(" ").toInt())
				else -> throw IllegalArgumentException("Unknown instruction: $s")
			}
		}

		object Noop : Instruction()
		data class AddX(val value: Int) : Instruction()
	}
}