package ch.mikewong.adventofcode.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day24 : Day<Long, Long>(2021, 24, "Arithmetic Logic Unit") {

	private val instructionBlocks by lazy {
		val blockSize = inputLines.drop(1).indexOf("inp w") + 1
		inputLines.chunked(blockSize).map { block ->
			val xAddition = block.last { it.startsWith("add x") }.split(" ").last().toInt()
			val yAddition = block.last { it.startsWith("add y") }.split(" ").last().toInt()
			val zDivision = block.single { it.startsWith("div z") }.split(" ").last().toInt()
			InstructionsBlock(xAddition, yAddition, zDivision)
		}
	}

	override fun partOne() = findMonadDigit(9 downTo 1)

	override fun partTwo() = findMonadDigit(1..9)

	private fun findMonadDigit(range: IntProgression): Long {
		val monadDigits = mutableListOf<Int>()
		findMonadDigit(range, monadDigits, 0)
		return monadDigits.joinToString("").toLong()
	}

	private fun findMonadDigit(range: IntProgression, monadDigits: MutableList<Int>, currentZ: Int): Boolean {
		val blockIndex = monadDigits.size

		// If the last block is processed, check if the z value is zero
		if (blockIndex == instructionBlocks.size) {
			return currentZ == 0
		}

		var z = currentZ
		val block = instructionBlocks[blockIndex]
		for (digit in range) {
			val x = if (((currentZ % 26) + block.xAddition) != digit) 1 else 0

			if (block.zDivision == 1 || x != 1) {
				z /= block.zDivision
				z *= (25 * x + 1)

				val y = (digit + block.yAddition) * x
				z += y

				monadDigits.add(digit)
				if (findMonadDigit(range, monadDigits, z)) {
					return true
				}

				monadDigits.removeLast()

				z = currentZ
			}
		}

		return false
	}

	/**
	 * Each instruction block only differs in the values of three instructions: the x addition, y addition and z division
	 */
	private data class InstructionsBlock(val xAddition: Int, val yAddition: Int, val zDivision: Int)

}