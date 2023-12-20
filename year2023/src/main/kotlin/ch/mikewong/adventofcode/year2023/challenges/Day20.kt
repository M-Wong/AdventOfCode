package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.countWhile
import ch.mikewong.adventofcode.common.extensions.lcm

class Day20 : Day<Int, Long>(2023, 20, "Pulse Propagation") {

	private val modules = readInput()

	override fun partOne(): Int {
		val buttonPresses = 1000

		// Count the number of pulses for each button press, then sum it all up
		val (totalLow, totalHigh) = (1..buttonPresses).map {
			countPulses()
		}.reduce { acc, pulses -> acc + pulses }

		return totalLow * totalHigh
	}

	override fun partTwo(): Long {
		if (isControlSet) return 0 // Test set doesn't have a final rx module

		// Get the module before the RX sink (which is a conjunction in the input)
		val outputModule = "rx"
		val finalConjunction = modules.values
			.filterIsInstance<Module.Conjunction>()
			.single { it.destinations == listOf(outputModule) }

		// Find the number of required button presses that each input module of this conjunction requires to send a high puls
		val requiredButtonPresses = finalConjunction.inputPulses.keys.map { inputModule ->
			resetModules()

			// Count the number of times until the target pulse matches
			countWhile(
				condition = { found -> !found },
				block = {
					findTargetModuleAndPulse(inputModule, targetPulse = true)
				}
			).toLong()
		}

		return lcm(requiredButtonPresses)
	}

	private fun readInput(): Map<String, Module> {
		val modules = inputLines.associate { line ->
			val (moduleName, outputs) = line.split(" -> ")
			val outputModules = outputs.split(", ")
			val module = when {
				moduleName == "broadcaster" -> Module.Broadcaster(moduleName, outputModules)
				moduleName.startsWith("%") -> Module.FlipFlop(moduleName.substring(1), outputModules)
				moduleName.startsWith("&") -> Module.Conjunction(moduleName.substring(1), outputModules)
				else -> throw IllegalArgumentException("Unknown module name: $moduleName")
			}
			module.name to module
		}.withDefault { Module.Noop }

		modules.values.filterIsInstance<Module.Conjunction>().forEach { conjunction ->
			val inputModules = modules.values.filter { it.destinations.contains(conjunction.name) }
			inputModules.forEach { module ->
				conjunction.inputPulses[module.name] = false
			}
		}

		return modules
	}

	private fun resetModules() {
		modules.values.forEach { module ->
			when (module) {
				is Module.Noop -> {} // NOOP
				is Module.Broadcaster -> {} // NOOP
				is Module.Conjunction -> module.inputPulses.forEach { (k, _) -> module.inputPulses[k] = false }
				is Module.FlipFlop -> module.isTurnedOn = false
			}
		}
	}

	private fun countPulses(): Pair<Int, Int> {
		// Start with a low pulse from the broadcaster
		val startModule = modules.getValue("broadcaster")
		val queuedPulses = mutableListOf(Pulse("", startModule, false))

		var lowPulses = 0
		var highPulses = 0

		while (queuedPulses.isNotEmpty()) {
			// Process the next pulse
			val (source, module, inputPulse) = queuedPulses.removeFirst()

			// Increase the correct pulse counter
			if (inputPulse) {
				highPulses++
			} else {
				lowPulses++
			}

			// Let the module convert the input pulse to an output pulse
			val outputPulse = module.convertPulse(source, inputPulse) ?: continue

			// Create pulses to the module destinations and add them to the queue
			module.destinations.map { modules.getValue(it) }.forEach { nextModule ->
				queuedPulses.add(Pulse(module.name, nextModule, outputPulse))
			}

		}

		return lowPulses to highPulses
	}

	private fun findTargetModuleAndPulse(targetModule: String, targetPulse: Boolean): Boolean {
		// Start with a low pulse from the broadcaster
		val startModule = modules.getValue("broadcaster")
		val queuedPulses = mutableListOf(Pulse("", startModule, false))

		while (queuedPulses.isNotEmpty()) {
			// Process the next pulse
			val (source, module, inputPulse) = queuedPulses.removeFirst()

			// Let the module convert the input pulse to an output pulse
			val outputPulse = module.convertPulse(source, inputPulse) ?: continue

			// If the module and its output pulse matches the target module pulse, return true
			if (module.name == targetModule && outputPulse == targetPulse) {
				return true
			}

			// Otherwise create pulses to the module destinations and add them to the queue
			module.destinations.map { modules.getValue(it) }.forEach { nextModule ->
				queuedPulses.add(Pulse(module.name, nextModule, outputPulse))
			}
		}

		// If all modules were fully processed and the target module and pulse didn't match, return false
		return false
	}

	private operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = Pair(this.first + other.first, this.second + other.second)

	private data class Pulse(val source: String, val module: Module, val pulse: Boolean)

	private sealed interface Module {
		val name: String
		val destinations: List<String>

		fun convertPulse(source: String, inputPulse: Boolean): Boolean?

		/** NOOP modules return no pulse */
		data object Noop : Module {
			override val name = "NOOP"
			override val destinations = emptyList<String>()

			override fun convertPulse(source: String, inputPulse: Boolean) = null
		}

		/** Broadcaster modules return the same input pulse */
		class Broadcaster(override val name: String, override val destinations: List<String>) : Module {
			override fun convertPulse(source: String, inputPulse: Boolean) = inputPulse
		}

		class FlipFlop(override val name: String, override val destinations: List<String>) : Module {
			var isTurnedOn: Boolean = false

			/**
			 * FlipFlop modules ignore high pulses and toggle their state for low pulses, then return high or low based on their new state
			 */
			override fun convertPulse(source: String, inputPulse: Boolean): Boolean? {
				return if (inputPulse) {
					null
				} else {
					isTurnedOn = !isTurnedOn
					isTurnedOn
				}
			}
		}

		class Conjunction(override val name: String, override val destinations: List<String>) : Module {
			val inputPulses = mutableMapOf<String, Boolean>()

			/**
			 * Conjunction modules store the last input pulse per [source] and return a low pulse if all sources sent a high pulse last, otherwise a high pulse
			 */
			override fun convertPulse(source: String, inputPulse: Boolean): Boolean {
				inputPulses[source] = inputPulse
				return !inputPulses.values.all { it }
			}
		}
	}

}