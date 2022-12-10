package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day10 : Day<Int, String>(2022, 10, "Cathode-Ray Tube") {

	private val cycles by lazy { computeCycles() }

	override fun partOne(): Int {
		val interestingCycles = 20 until cycles.size step 40
		return interestingCycles.sumOf { cycles[it] * it }
	}

	override fun partTwo(): String {
		val crt = buildString {
			cycles.drop(1).chunked(40).forEach { crtLine ->
				crtLine.forEachIndexed { idx, cycle ->
					if (idx % 40 in (cycle - 1 .. cycle + 1)) {
						append("██")
					} else {
						append("░░")
					}
				}
				appendLine()
			}
		}
		println(crt)
		return "RZHFGJCB"
	}

	private fun computeCycles(): List<Int> {
		var registerValue = 1

		// The program starts already at cycle 1 and register value 1, to offset the zero-index, add an irrelevant value at index 0
		val cycles = mutableListOf(0, registerValue)

		inputLines.forEach { line ->
			when (val instruction = Instruction.fromString(line)) {
				is Instruction.Noop -> {
					cycles.add(registerValue)
				}
				is Instruction.AddX -> {
					cycles.add(registerValue)
					registerValue += instruction.value
					cycles.add(registerValue)
				}
			}
		}

		return cycles

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