package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.set
import java.util.*
import kotlin.math.ceil

class Day16 : Day<Int, Int>(2022, 16, "Proboscidea Volcanium") {

	private val valves by lazy { readInput() }
	private val startValve = "AA"

	override fun partOne() = findMaxPressure(valves, 1, 30)

	override fun partTwo() = findMaxPressure(valves, 2, 26)

	private fun findMaxPressure(valves: Map<String, Valve>, playerCount: Int, timer: Int): Int {
		// All players start at the same valve
		val start = List(playerCount) { startValve }

		// Cache from player positions to released pressure
		val maxReleasedPressuresForPositions = mutableMapOf(start to 0)

		// Queue with an initial valve state (multiply the timer with the player count to get an alternating move pattern)
		val queue = ArrayDeque(listOf(ValveState(start, 0, emptySet(), timer * playerCount)))

		// Set of new valve states that arise from processing the previous queue
		val newValveStates = mutableSetOf<ValveState>()

		while (queue.isNotEmpty() || newValveStates.isNotEmpty()) {
			if (queue.isEmpty()) {
				queue.addAll(newValveStates)
				newValveStates.clear()
			}

			val state = queue.removeFirst()

			// The player that is about to move or open a valve and the valve he is currently at
			val playerIndex = state.cumulativeRemainingTime % playerCount
			val valveAtPlayerPosition = valves.getValue(state.playerPositions[playerIndex])

			// The actual time left
			val time = ceil(state.cumulativeRemainingTime.toFloat() / playerCount).toInt()

			// The currently known and the new maximum released pressure for the current player positions
			val currentMaxReleasedPressure = maxReleasedPressuresForPositions.getOrDefault(state.playerPositions, 0)
			val newReleasedPressure = state.releasedPressure + valveAtPlayerPosition.flowRate * (time - 1)

			when {
				state.cumulativeRemainingTime <= 0 -> {
					// No time left, continue next state in the queue
					continue
				}
				valveAtPlayerPosition.flowRate == 0 -> {
					// Ignore valves with no flow rate
				}
				state.playerPositions[playerIndex] in state.openValves -> {
					// Ignore valve that is aleady open
				}
				newReleasedPressure > currentMaxReleasedPressure -> {
					// The new calculated released pressure is higher than the currently known max for these player positions
					maxReleasedPressuresForPositions[state.playerPositions] = newReleasedPressure
					val newValveState = ValveState(state.playerPositions, newReleasedPressure, state.openValves + valveAtPlayerPosition.name, state.cumulativeRemainingTime - 1)
					newValveStates.addPotentialNewState(newValveState, maxReleasedPressuresForPositions)
				}
				else -> {
					// Ignore valves that did not release more pressure than a previous path
				}
			}

			// For each connection to another valve, create a new valve state with the current player moving to that valve
			valveAtPlayerPosition.connections.forEach { valveKey ->
				val newPlayerPositions = state.playerPositions.set(playerIndex, valveKey)
				val newValveState = ValveState(newPlayerPositions, state.releasedPressure, state.openValves, state.cumulativeRemainingTime - 1)
				newValveStates.addPotentialNewState(newValveState, maxReleasedPressuresForPositions)
			}
		}

		return maxReleasedPressuresForPositions.values.max()
	}

	/**
	 * Adds [state] to this set of ValveStates if is has no currently known max released pressure or has a potential higher pressure release than before
	 */
	private fun MutableSet<ValveState>.addPotentialNewState(state: ValveState, maxReleasedPressuresForPositions: Map<List<String>, Int>) {
		val playerPositionsWithClosedValves = state.playerPositions.filter { it !in state.openValves }.distinct()
		val releasedPressure = playerPositionsWithClosedValves.sumOf { valves.getValue(it).flowRate } * (state.cumulativeRemainingTime - 1)

		if (
			state.playerPositions !in maxReleasedPressuresForPositions
			|| releasedPressure + state.releasedPressure >= maxReleasedPressuresForPositions.getValue(state.playerPositions)
		) {
			this.add(state)
		}
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

	private data class ValveState(
		val playerPositions: List<String>,
		val releasedPressure: Int,
		val openValves: Set<String>,
		val cumulativeRemainingTime: Int,
	) : Comparable<ValveState> {
		override fun compareTo(other: ValveState) = this.releasedPressure.compareTo(other.releasedPressure) * -1
	}

}