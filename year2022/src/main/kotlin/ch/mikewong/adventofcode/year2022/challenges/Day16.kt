package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day

/**
 * Part 1: 1653
 * Part 2: 2223
 */
class Day16 : Day<Int, Int>(2022, 16, "Proboscidea Volcanium") {

	private val valves by lazy { readInput() }
	private val startValve = "AA"

	override fun partOne(): Int {
		val timer = 30

		// Keep a list of valve states, initializing with the starting valve, 0 release pressure and no open valves
		var states = mutableSetOf(ValveState(startValve, 0, emptySet()))

		// Cache the maximum pressure value of combinations between valves and open valves
		val maxPressureCache = mutableMapOf<Pair<String, Set<String>>, Int>()

		// Start the timer down to 1, because the
		(timer downTo 1).forEach { t ->
			val newStates = mutableSetOf<ValveState>()

			// For each existing valve state, iterate through and create new states
			states.forEach { state ->
				val key = state.valve to state.openValves

				// Only process if the maximum pressure has not yet been calculated or is lower than the one of the current valve state
				if (maxPressureCache[key] == null || state.releasedPressure > maxPressureCache[key]!!) {
					maxPressureCache[key] = state.releasedPressure

					val valve = valves[state.valve]!!

					// If the current valve is not open, and it has a non-zero flow rate, create a new valve state with the updated released pressure
					if (!state.openValves.contains(state.valve) && valve.flowRate > 0) {
						val newOpenValves = state.openValves.plus(state.valve)
						// The released pressure of the new valve state is the previously released pressure plus the valve flow rate
						// multiplied with the current timer (minus 1 because that state is only checked in the next iteration) to
						// calculate the pressure release until the end of the eruption
						val newValveState = ValveState(state.valve, state.releasedPressure + valve.flowRate * (t - 1), newOpenValves)
						newStates.add(newValveState)
					}

					// For each connection to another valve, create a new valve state for that connection
					valve.connections.forEach {
						newStates.add(ValveState(it, state.releasedPressure, state.openValves))
					}
				}
			}

			states = newStates
		}

		return states.maxOf { it.releasedPressure }
	}

	override fun partTwo(): Int {
		return 0
	}

	private fun readInput(): Map<String, Valve> {
		val regex = "Valve (\\w*) has flow rate=(\\d*); tunnels? leads? to valves? (.*)".toRegex()

		return inputLines.associate { line ->
			val matches = regex.matchEntire(line)!!
			val (_, name, flowRate, valveKeys) = matches.groupValues
			val connections = valveKeys.split(", ").toSet()
			name to Valve(name, flowRate.toInt(), connections)
		}
	}

	private data class Valve(val name: String, val flowRate: Int, val connections: Set<String>)

	private data class ValveState(val valve: String, val releasedPressure: Int, val openValves: Set<String>)
}