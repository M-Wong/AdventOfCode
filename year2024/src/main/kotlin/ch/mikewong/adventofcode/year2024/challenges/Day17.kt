package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asLongs
import kotlin.math.pow

class Day17 : Day<String, Long>(2024, 17, "Chronospatial Computer") {

	private val registers by lazy { readRegisters() }
	private val operations by lazy { readOperations() }

	// 3,6,3,7,0,7,0,3,0
	override fun partOne(): String {
		// evaluateProgram is a more or less hardcoded calculation based on the example input and my main input
		return evaluateProgram(registers.getValue('A'))

		// executeOpCodes is an acutal generic implementation, but it's a bit slower (and only useful for part 1 anyway)
//		return executeOpCodes(registers, operations)
	}

	// 136904920099226
	override fun partTwo(): Long {
		/*
		Reverse engineering the example input:
		Initial registers:
		A = 2024
		B = 0
		C = 0

		Each operation written out:							Example value (first evaluation)
		0,3 -> A = A / 2^3	or A = A >> 3					A = 2024 / 2^3 = 253
		5,4 -> Output A % 8									Output = 253 % 8 = 5
		3,0 -> Jump to 0

		or boiled down into a single operation:
		Output 	= A % 8
				= (A >> 3) % 8

		===================================================

		Reverse engineering the main input:
		Initial registers:
		A = 51064159
		B = 0
		C = 0

		Each operation written out:							Example value (first evaluation)
		2,4 -> B = A % 8									B = 51064159 % 8 = 7
		1,5 -> B = B xor 5									B = 7 xor 5 = 2
		7,5 -> C = A / 2^B  or C = A >> B					C = 51064159 / 2^2 = 12766039
		1,6 -> B = B xor 6									B = 2 xor 6 = 4
		0,3 -> A = A / 2^3	or A = A >> 3					A = 51064159 / 2^3 = 6383019
		4,6 -> B = B xor C									B = 4 xor 12766039 = 12766035
		5,5 -> Output B % 8									Output = 12766035 % 8 = 3
		3,0 -> Jump to 0

		or boiled down into a single operation:
		Output 	= B % 8
				= (B xor C) % 8
				= ((B xor 6) xor C) % 8
				= (((B xor 5) xor 6) xor C) % 8
				= ((((A % 8) xor 5) xor 6) xor C) % 8
				= ((((A % 8) xor 5) xor 6) xor (A >> B)) % 8
				= ((((A % 8) xor 5) xor 6) xor (A >> (B xor 5))) % 8
				= ((((A % 8) xor 5) xor 6) xor (A >> ((A % 8) xor 5))) % 8

		===================================================

		Based on the above reverse engineering, we can go from the original code backwards and find the possible register inputs,
		that would result in the original program code as output.
		*/

		val originalCode = inputLines.last().substringAfter("Program: ")

		// Keep a list of all possible inputs that result in the exact same output as the program input
		val possibleInputs = mutableSetOf(0L)

		// Iterate on the original code in reverse order
		originalCode.split(",").asLongs().reversed().forEach { expectedOutput ->

			// Calculate the new possible inputs that result in the expected output
			val newPossibleInputs = mutableSetOf<Long>()
			possibleInputs.forEach { currentInput ->

				// The output is calculated with modulo 8, meaning it outputs only the last three bits of a number
				// Iterate over these bits...
				(0..8).forEach { lastBits ->
					// to find the original numbers...
					val newInput = (currentInput shl 3) + lastBits

					// ... then calculate the output of that number and check if it matches the expected output.
					val actualOutput = calculateOutput(newInput)
					if (actualOutput == expectedOutput) {
						// If it does, this number is a possible input and needs to be checked for the next value in the original code
						newPossibleInputs.add(newInput)
					}
				}
			}

			possibleInputs.clear()
			possibleInputs.addAll(newPossibleInputs)
		}

		return possibleInputs.min()
	}

	private fun readRegisters(): Map<Char, Long> {
		return inputGroups.first().associate { line ->
			val (register, value) = line.substringAfter("Register ").split(": ")
			register.first() to value.toLong()
		}
	}

	private fun readOperations(): List<OpCode> {
		return inputGroups.last().flatMap { line ->
			line.substringAfter("Program: ").split(",").chunked(2).map { (code, operand) ->
				OpCode.fromCode(code.toInt(), operand.toLong())
			}
		}
	}

	/**
	 * Evaluates the entire program, which only needs the initial value of register A
	 */
	private fun evaluateProgram(initialA: Long): String {
		var a = initialA

		val output = mutableListOf<String>()

		while (a != 0L) {
			output.add(calculateOutput(a).toString())
			a = a shr 3
		}

		return output.joinToString(",")
	}

	/**
	 * Calculates the OpCode output based on the register [a] value.
	 * This calculation is hardcoded for the example input and my input (see partTwo() for the explanation)
	 */
	private fun calculateOutput(a: Long): Long {
		return if (isControlSet) {
			(a shr 3) % 8
		} else {
			((((a % 8) xor 5) xor 6) xor (a shr ((a % 8) xor 5).toInt())) % 8
		}
	}

	/**
	 * Executes the actual OpCode operations one by one.
	 * This was my first implementation, which is generic but slow, so I'm only using it for the example input
	 */
	private fun executeOpCodes(inputRegisters: Map<Char, Long>, operations: List<OpCode>): String {
		val registers = inputRegisters.toMutableMap()
		val output = mutableListOf<String>()
		var instructionPointer = 0

		while (instructionPointer < operations.size) {
			when (val op = operations[instructionPointer]) {
				is OpCode.Adv -> {
					val operandValue = getComboOperandValue(op.operand, registers)
					registers['A'] = (registers.getValue('A') / 2.0.pow(operandValue.toDouble())).toLong()
					instructionPointer++
				}
				is OpCode.Bxl -> {
					registers['B'] = registers.getValue('B') xor op.operand
					instructionPointer++
				}
				is OpCode.Bst -> {
					val operandValue = getComboOperandValue(op.operand, registers)
					registers['B'] = operandValue % 8
					instructionPointer++
				}
				is OpCode.Jnz -> {
					val registerValue = registers.getValue('A')
					instructionPointer = if (registerValue != 0L) {
						op.operand.toInt() / 2
					} else {
						instructionPointer + 1
					}
				}
				is OpCode.Bxc -> {
					registers['B'] = registers.getValue('B') xor registers.getValue('C')
					instructionPointer++
				}
				is OpCode.Out -> {
					val operandValue = getComboOperandValue(op.operand, registers)
					output.add((operandValue % 8).toString())
					instructionPointer++
				}
				is OpCode.Bdv -> {
					val operandValue = getComboOperandValue(op.operand, registers)
					registers['B'] = (registers.getValue('A') / 2.0.pow(operandValue.toDouble())).toLong()
					instructionPointer++
				}
				is OpCode.Cdv -> {
					val operandValue = getComboOperandValue(op.operand, registers)
					registers['C'] = (registers.getValue('A') / 2.0.pow(operandValue.toDouble())).toLong()
					instructionPointer++
				}
			}
		}

		return output.joinToString(",")
	}

	private fun getComboOperandValue(operand: Long, registers: Map<Char, Long>): Long {
		return when (operand) {
			in 0..3 -> operand
			4L -> registers.getValue('A')
			5L -> registers.getValue('B')
			6L -> registers.getValue('C')
			7L -> throw IllegalArgumentException("Combo operand 7 is reserved")
			else -> throw IllegalArgumentException("Invalid operand value: $operand")
		}
	}

	sealed interface OpCode {
		companion object {
			fun fromCode(code: Int, operand: Long): OpCode {
				return when (code) {
					0 -> Adv(operand)
					1 -> Bxl(operand)
					2 -> Bst(operand)
					3 -> Jnz(operand)
					4 -> Bxc
					5 -> Out(operand)
					6 -> Bdv(operand)
					7 -> Cdv(operand)
					else -> throw IllegalArgumentException("Unknown OpCode: $code")
				}
			}
		}

		data class Adv(val operand: Long) : OpCode
		data class Bxl(val operand: Long) : OpCode
		data class Bst(val operand: Long) : OpCode
		data class Jnz(val operand: Long) : OpCode
		data object Bxc : OpCode
		data class Out(val operand: Long) : OpCode
		data class Bdv(val operand: Long) : OpCode
		data class Cdv(val operand: Long) : OpCode
	}

}