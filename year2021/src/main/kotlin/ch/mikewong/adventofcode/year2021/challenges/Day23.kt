package ch.mikewong.adventofcode.year2021.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.set
import java.util.*
import kotlin.math.abs

class Day23 : Day<Int, Int>(2021, 23, "Amphipod") {

	private val burrowCount = 4

	override fun partOne(): Int {
		val burrows = initializeBurrows(0, 2)
		return findLowestEnergyConsumption(burrows)
	}

	override fun partTwo(): Int {
		val burrows = initializeBurrows(1, 4)
		return findLowestEnergyConsumption(burrows)
	}

	/**
	 * Initialize the different burrows with their occupants from the input
	 */
	private fun initializeBurrows(group: Int, size: Int): List<Burrow> {
		val initialAmphipods = parseAmphipods(group)
		val burrowOccupants = (0 until burrowCount).map { i -> initialAmphipods.map { it[i]!! } }
		return listOf(
			Burrow(size, burrowOccupants[0], 2) { it is Amphipod.Amber },
			Burrow(size, burrowOccupants[1], 4) { it is Amphipod.Bronze },
			Burrow(size, burrowOccupants[2], 6) { it is Amphipod.Copper },
			Burrow(size, burrowOccupants[3], 8) { it is Amphipod.Desert },
		)
	}

	/**
	 * Parse the specified input group into a list of amphipods, chunked into lists of [burrowCount] elements (basically the rows)
	 */
	private fun parseAmphipods(group: Int): List<List<Amphipod?>> {
		return inputGroups[group].map { line ->
			line.filter { it in "ABCD" }.map { c ->
				when (c) {
					'A' -> Amphipod.Amber
					'B' -> Amphipod.Bronze
					'C' -> Amphipod.Copper
					'D' -> Amphipod.Desert
					else -> null
				}
			}
		}.flatten().chunked(burrowCount)
	}

	private fun findLowestEnergyConsumption(burrows: List<Burrow>): Int {
		// Initialize a priority queue (which uses the energy consumption of a given board state) with the input data
		val boardStateQueue = PriorityQueue<BoardState>()
		val initialHallway = List<Amphipod?>(11) { null }
		boardStateQueue.add(BoardState(0, burrows, initialHallway))

		val visitedBoardStates = mutableSetOf<Pair<List<Burrow>, List<Amphipod?>>>()
		while (boardStateQueue.isNotEmpty()) {
			val boardState = boardStateQueue.poll()
			val allBurrowEntrances = boardState.burrows.map { it.entrance }

			// If the board state is terminal, return the energy consumption
			if (boardState.isTerminal()) {
				return boardState.energyConsumptionSoFar
			}

			// If this board state has already occurred skip it, otherwise add it to the visited board states
			if (visitedBoardStates.contains(boardState.fieldState())) {
				continue
			} else {
				visitedBoardStates.add(boardState.fieldState())
			}

			// Generate the possible next board states by moving amphipods from a burrow into the hallway
			boardState.burrows.forEachIndexed { burrowIndex, burrow ->
				// Only try to move an amphipod into the hallway if the burrow is not empty and contains unacceptable amphipods
				if (burrow.occupants.isNotEmpty() && burrow.containsUnacceptableAmphipods()) {
					val amphipod = burrow.occupants.first()
					val burrowEntrance = allBurrowEntrances[burrowIndex]

					// Get the hallway positions to the left and right of the burrow entrance, minus all the other entrances
					val hallwayToLeft = (burrowEntrance downTo 0) - allBurrowEntrances
					val hallwayToRight = (burrowEntrance until boardState.hallway.size) - allBurrowEntrances
					val hallwayDirections = listOf(hallwayToLeft, hallwayToRight)
					hallwayDirections.forEach { direction ->
						// Only take hallway positions that are empty and move the amphipod to that position, calculating the new board state
						direction.takeWhile { boardState.hallway[it] == null }
							.forEach { hallwayPosition ->
								val steps = burrow.emptySpace() + abs(burrowEntrance - hallwayPosition) + 1

								val newEnergyConsumption = boardState.energyConsumptionSoFar + steps * amphipod.energyConsumption
								val newBoardState = BoardState(
									newEnergyConsumption,
									boardState.burrows.set(burrowIndex, burrow.copy(occupants = burrow.occupants.drop(1))), // Remove the first occupand in the burrow, which is the amphipod we're moving right now
									boardState.hallway.set(hallwayPosition, amphipod) // Move the amphipod to the hallway position
								)

								if (!visitedBoardStates.contains(newBoardState.fieldState())) {
									boardStateQueue.add(newBoardState)
								}
							}
					}
				}
			}

			// Generate the possible next board states by moving amphipods from the hallway into their target burrow
			boardState.hallway.forEachIndexed { hallwayPosition, amphipod ->
				// Can't use filterNotNull() here because we need the correct hallway position
				if (amphipod != null) {
					val targetBurrow = boardState.burrowForAmphipod(amphipod)

					// If the amphipod can enter its target burrow, calculate the new board state
					if (canEnterBurrow(boardState, hallwayPosition, targetBurrow)) {
						val steps = targetBurrow.emptySpace() + abs(targetBurrow.entrance - hallwayPosition)

						val newEnergyConsumption = boardState.energyConsumptionSoFar + steps * amphipod.energyConsumption
						val newBoardState = BoardState(
							newEnergyConsumption,
							boardState.burrows.set(
								targetBurrow,
								targetBurrow.copy(occupants = targetBurrow.occupants.plus(amphipod))
							),
							boardState.hallway.set(hallwayPosition, null)
						)

						if (!visitedBoardStates.contains(newBoardState.fieldState())) {
							boardStateQueue.add(newBoardState)
						}
					}
				}
			}
		}

		return Int.MAX_VALUE
	}

	/**
	 * Check if the target burrow is enterable
	 */
	private fun canEnterBurrow(boardState: BoardState, fromHallwayPosition: Int, targetBurrow: Burrow): Boolean {
		// If the burrow is full, it cannot be entered
		if (targetBurrow.emptySpace() == 0) return false

		// If the burrow contains any unacceptable amphipods, it cannot be entered
		if (targetBurrow.containsUnacceptableAmphipods()) return false

		return if (fromHallwayPosition < targetBurrow.entrance) {
			// If the burrow is to the right of the hallway position, check if all hallway fields in between are empty
			boardState.hallway.subList(fromHallwayPosition + 1, targetBurrow.entrance).all { it == null }
		} else if (fromHallwayPosition > targetBurrow.entrance) {
			// If the burrow is to the left of the hallway position, check if all hallway fields in between are empty
			boardState.hallway.subList(targetBurrow.entrance + 1, fromHallwayPosition).all { it == null }
		} else {
			// Invalid case, should never occur
			false
		}
	}

	sealed class Amphipod(private val identifier: String, val energyConsumption: Int) {
		override fun toString() = identifier

		data object Amber : Amphipod("A", 1)
		data object Bronze : Amphipod("B", 10)
		data object Copper : Amphipod("C", 100)
		data object Desert : Amphipod("D", 1000)
	}

	private data class Burrow(val size: Int, val occupants: List<Amphipod>, val entrance: Int, val accepts: (Amphipod) -> Boolean) {
		/**
		 * A burrow is terminal if the number of occupants equals its size and all occupants are accepted
		 */
		fun isTerminal() = occupants.size == size && occupants.all { accepts.invoke(it) }

		/**
		 * Returns if this burrow contains any amphipods that are not accepted
		 */
		fun containsUnacceptableAmphipods() = occupants.any { !accepts.invoke(it) }

		/**
		 * Returns the amount of empty space in this burrow
		 */
		fun emptySpace() = size - occupants.size
	}

	private data class BoardState(
		val energyConsumptionSoFar: Int,
		val burrows: List<Burrow>,
		val hallway: List<Amphipod?>,
	) : Comparable<BoardState> {
		override fun compareTo(other: BoardState): Int {
			return this.energyConsumptionSoFar.compareTo(other.energyConsumptionSoFar)
		}

		override fun toString(): String {
			return "[energyUsed=$energyConsumptionSoFar, burrows=${burrows.map { it.occupants }}, hallway=$hallway]"
		}

		/**
		 * A board state is terminal if all its burrows are terminal
		 */
		fun isTerminal() =  burrows.all { it.isTerminal() }

		/**
		 * Find the burrow that accepts the given [amphipod]
		 */
		fun burrowForAmphipod(amphipod: Amphipod) = burrows.single { it.accepts(amphipod) }

		/**
		 * Return a pair containing the actual field state for caching (since the energy consumption is not relevant for that)
		 */
		fun fieldState() = burrows to hallway
	}

}