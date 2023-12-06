package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.set

class Day8 : Day<Int, Int>(2020, 8, "Handheld Halting") {

	private val instructions = inputLines.map { line ->
		val (instruction, value) = line.split(" ")
		val type = enumValueOf<InstructionType>(instruction.uppercase())
		Instruction(type, value.toInt())
	}

	override fun partOne(): Int {
		return requireNotNull(executeInstructions(instructions, true))
	}

	override fun partTwo(): Int {
		return instructions.indices.filter {
			// Get the indices for NOOP and JUMP instructions
			instructions[it].type == InstructionType.NOP || instructions[it].type == InstructionType.JMP
		}.map { idx ->
			// Create a list of lists of instructions where a single instruction is replaced
			val old = instructions[idx]
			val new = old.copy(type = if (old.type == InstructionType.JMP) InstructionType.NOP else InstructionType.JMP)
			instructions.set(idx, new)
		}.firstNotNullOf { executeInstructions(it, false) }
	}

	/**
	 * Execute the provided [instructions] and return the accumulator if it terminates regularly.
	 * If a loop is detected return either the accumulator or null, depending on the [returnAccumulatorOnLoop] flag
	 */
	private fun executeInstructions(instructions: List<Instruction>, returnAccumulatorOnLoop: Boolean): Int? {
		var accumulator = 0
		var instructionIndex = 0
		val executedInstructions = BooleanArray(instructions.size)

		// Execute instructions until an instruction is repeated or doesn't exist
		while (instructionIndex in instructions.indices && !executedInstructions[instructionIndex]) {
			val instruction = instructions[instructionIndex]
			if (instruction.type == InstructionType.ACC) {
				accumulator += instruction.amount
			}
			executedInstructions[instructionIndex] = true
			instructionIndex += instruction.offset
		}

		return when {
			// If the next instruction to be executed doesn't exist, return the accumulator
			instructionIndex !in instructions.indices -> accumulator

			// If the next instruction was already executed, return the accumulator for part one, but return null for part two
			executedInstructions[instructionIndex] -> accumulator.takeIf { returnAccumulatorOnLoop }

			// Other cases should never happen
			else -> throw IllegalStateException("Should never happen")
		}
	}

	private enum class InstructionType {
		NOP, ACC, JMP
	}

	private data class Instruction(val type: InstructionType, val amount: Int) {
		val offset get() = if (type == InstructionType.JMP) amount else 1
	}

}