package ch.mikewong.adventofcode.year2025.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import java.util.*

class Day10 : Day<Int, Int>(2025, 10, "Factory") {

	private val machines by lazy { readInput() }

	/** 481 */
	override fun partOne(): Int {
		val finalMachineStates = machines.map { machine ->
			machine.calculateState()
		}

		return finalMachineStates.sumOf { it.buttonPresses }
	}

	override fun partTwo(): Int {
		return 0
	}

	private fun readInput(): List<Machine> {
		return inputLines.map { line ->
			val parts = line.split(" ")

			// Parse light diagram into a BitSet
			val lightString = parts.first().trim('[', ']')
			val lights = BitSet()
			lightString.forEachIndexed { idx, c -> lights[idx] = (c == '#') }

			// Parse wirings
			val wirings = parts.drop(1).dropLast(1)
				.mapIndexed { idx, wiring ->
					val connections = wiring.trim('(', ')').split(",").map { it.toInt() }.toSet()
					Wiring(from = idx, to = connections)
				}

			// Parse joltage
			val joltage = parts.last().trim('{', '}').split(",").map { it.toInt() }

			Machine(
				lightDiagram = lights,
				wirings = wirings,
				joltage = joltage,
			)
		}
	}

	private fun Machine.calculateState(): MachineState {
		val initialLights = BitSet(lightDiagram.size())
		val queue = PriorityQueue<MachineState>()

		// Press every button once initially
		queue.addAll(this.getStates(initialLights, 0))

		while (queue.isNotEmpty()) {
			val state = queue.poll()
			if (state.lights == this.lightDiagram) {
				// Lights match the diagram, we reached a correct state
				return state
			} else {
				// Add all new states from pressing all buttons
				queue.addAll(this.getStates(state.lights, state.buttonPresses, state.lastButton))
			}
		}

		throw IllegalStateException("Found no solution for machine: $lightDiagram")
	}

	private fun Machine.getStates(currentLights: BitSet, buttonPresses: Int, lastButton: Wiring? = null) = buildList {
		wirings.forEach { wiring ->
			// No need to press the same button again, as that would only reverse the change
			if (wiring != lastButton) {
				val lights = currentLights.press(wiring)
				add(MachineState(lights, buttonPresses + 1, wiring))
			}
		}
	}

	/** Create a new BitSet where every bit of the [wiring] is flipped */
	private fun BitSet.press(wiring: Wiring): BitSet {
		return this.get(0, this.size()).apply {
			wiring.to.forEach { light ->
				this.flip(light)
			}
		}
	}

	private data class MachineState(
		val lights: BitSet,
		val buttonPresses: Int,
		val lastButton: Wiring,
	) : Comparable<MachineState> {

		override fun compareTo(other: MachineState): Int {
			return this.buttonPresses.compareTo(other.buttonPresses)
		}

	}

	private data class Machine(val lightDiagram: BitSet, val wirings: List<Wiring>, val joltage: List<Int>)

	private data class Wiring(val from: Int, val to: Set<Int>)

}