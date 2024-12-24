package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.swap

class Day24 : Day<Long, String>(2024, 24, "Crossed Wires") {

	private val initialWireValues by lazy {
		inputGroups.first().associate { line ->
			val (wire, value) = line.split(": ")
			wire to (value == "1")
		}
	}

	private val connections by lazy {
		inputGroups.last().associate { line ->
			val (gateString, output) = line.split(" -> ")
			val (left, operation, right) = gateString.split(" ")

			output to Gate(left, right, Operation.valueOf(operation))
		}
	}

	// 57344080719736
	override fun partOne(): Long {
		val outputWires = connections.keys.filter { it.startsWith("z") }

		val wireOutputs = outputWires.associateWith { simulateWire(it, connections) }
			.entries
			.sortedByDescending { it.key }
			.map { it.value }

		return wiresToNumber(wireOutputs)
	}

	// See resources/AoC 2024-24-P2.xlsx for how I solved this part
	// cgq,fnr,kqk,nbc,svm,z15,z23,z39
	override fun partTwo(): String {
		if (isControlSet) return "" // There's no real example for this one

		// Calculate the input x and y values and the expected output
		val xInput = initialWireValues
			.filterKeys { it.startsWith("x") }
			.entries
			.sortedByDescending { it.key }
			.map { it.value }
			.let { wiresToNumber(it) }

		val yInput = initialWireValues
			.filterKeys { it.startsWith("y") }
			.entries
			.sortedByDescending { it.key }
			.map { it.value }
			.let { wiresToNumber(it) }

		val expectedOutput = xInput + yInput

		// Filter the output wires (those that output to a z-wire)
		val outputWires = connections.keys.filter { it.startsWith("z") }

		// Swap the faulty connections
		val fixedConnections = connections.toMutableMap().apply {
			this.swap("z15", "kqk")
			this.swap("z23", "cgq")
			this.swap("z39", "fnr")
			this.swap("nbc", "svm")
		}

//		// Brute force solution to find the one faulty connection that can be swapped with nbc to produce the correct output
//		val gates = connections.toMutableMap().apply {
//			this.swap("z15", "kqk")
//			this.swap("z23", "cgq")
//			this.swap("z39", "fnr")
//		}
//		val gateToSwapWithNbc = gates.minus(listOf("z15, kqk", "z23", "cgq", "z39", "fnr", "nbc"))
//			.filterNot { it.key.startsWith("z") }
//			.map { gate ->
//				val newConnections = gates.toMutableMap().apply {
//					this.swap("nbc", gate.key)
//				}
//
//				val actualOutput = try {
//					val wireOutputs = outputWires.associateWith { simulateWire(it, newConnections) }
//						.entries
//						.sortedByDescending { it.key }
//						.map { it.value }
//
//					wiresToNumber(wireOutputs)
//				} catch (e: StackOverflowError) {
//					-1L
//				}
//
//				gate.key to (actualOutput == expectedOutput)
//			}
//			.single { it.second }
//			.first

		// Calculate the output of the fixed connections
		val wireOutputs = outputWires.associateWith { simulateWire(it, fixedConnections) }
			.entries
			.sortedByDescending { it.key }
			.map { it.value }

		val actualOutput = wiresToNumber(wireOutputs)
		assert(actualOutput == expectedOutput) { "Expected $expectedOutput, got $actualOutput" }

		return listOf(
			"cgq",
			"fnr",
			"kqk",
			"nbc",
			"svm",
			"z15",
			"z23",
			"z39"
		).sorted().joinToString(",") { it }
	}

	private fun simulateWire(wire: String, connections: Map<String, Gate>): Boolean {
		// We reached an input wire with an initial value
		if (initialWireValues.containsKey(wire)) return initialWireValues.getValue(wire)

		val gate = connections.getValue(wire)
		val leftWire = simulateWire(gate.left, connections)
		val rightWire = simulateWire(gate.right, connections)

		return when (gate.operation) {
			Operation.AND -> leftWire && rightWire
			Operation.OR -> leftWire || rightWire
			Operation.XOR -> leftWire xor rightWire
		}
	}

	private fun wiresToNumber(wires: List<Boolean>): Long {
		return wires.joinToString("") { it.toIntString() }.toLong(2)
	}

	data class Gate(val left: String, val right: String, val operation: Operation)

	enum class Operation {
		AND,
		OR,
		XOR,
	}

	private fun Boolean.toIntString() = if (this) "1" else "0"

}