package ch.mikewong.adventofcode.challenges

import kotlin.math.min

class Day21 : Day<Int, Long>(21, "Dirac Dice") {

	private val playerOneStartingPosition = inputLines.first().substringAfter(": ").toInt()
	private val playerTwoStartingPosition = inputLines.last().substringAfter(": ").toInt()

	private val diceRollSums = getPossibilities().map { it.sum() }

	private val winCountCache = hashMapOf<String, LongArray>()

	override fun partOne(): Int {
		val result = playGame { turn ->
			// Calculate the sum of the three dice rolls for the given turn
			val firstRoll = turn * 3
			(firstRoll % 100) + 1 + ((firstRoll + 1) % 100 + 1) + ((firstRoll + 2) % 100 + 1)
		}
		return result.diceRolls * min(result.playerOneScore, result.playerTwoScore)
	}

	override fun partTwo(): Long {
		val winCount = countWins(playerOneStartingPosition, playerTwoStartingPosition, 0, 0, 21)
		return winCount.maxOrNull()!!
	}

	private fun playGame(diceRollSum: (Int) -> Int): GameResult {
		var turn = 0
		var playerOneField = playerOneStartingPosition
		var playerTwoField = playerTwoStartingPosition
		var playerOneScore = 0
		var playerTwoScore = 0

		while (playerOneScore < 1000 && playerTwoScore < 1000) {
			val rollValue = diceRollSum.invoke(turn)

			// Advance the players to the next field and increase the score
			if (turn % 2 == 0) {
				playerOneField = ((playerOneField + rollValue - 1) % 10) + 1
				playerOneScore += playerOneField
			} else {
				playerTwoField = ((playerTwoField + rollValue - 1) % 10) + 1
				playerTwoScore += playerTwoField
			}

			turn++
		}

		return GameResult(3 * turn, playerOneScore, playerTwoScore)
	}

	/**
	 * Calculate a cartesian product of the 3 possible dice rolls
	 */
	private fun getPossibilities(): Set<List<Int>> {
		val values = (1..3)
		return mutableSetOf<List<Int>>().apply {
			values.forEach { a ->
				values.forEach { b ->
					values.forEach { c ->
						add(listOf(a, b, c))
					}
				}
			}
		}
	}

	private fun countWins(
		playerOnePosition: Int,
		playerTwoPosition: Int,
		playerOneScore: Int,
		playerTwoScore: Int,
		maxScore: Int
	): LongArray {
		val key = "$playerOnePosition-$playerTwoPosition-$playerOneScore-$playerTwoScore"

		// Check if this exact combination has already been calculated
		if (winCountCache.containsKey(key))  {
			return winCountCache[key]!!
		} else {
			val winCount = LongArray(2) { 0L }

			diceRollSums.forEach p1@{ playerOneRoll ->
				// Iterate the possible rolls for the first player
				val newPlayerOnePosition = (playerOnePosition + playerOneRoll - 1) % 10 + 1
				val newPlayerOneScore = playerOneScore + newPlayerOnePosition

				if (newPlayerOneScore >= maxScore) {
					winCount[0]++
					return@p1
				}

				diceRollSums.forEach p2@{ playerTwoRoll ->
					// Iterate the possible rolls for the second player
					val newPlayerTwoPosition = (playerTwoPosition + playerTwoRoll - 1) % 10 + 1
					val newPlayerTwoScore = playerTwoScore + newPlayerTwoPosition

					if (newPlayerTwoScore >= maxScore) {
						winCount[1]++
						return@p2
					}

					// If neither player has won, recursively call this method again with the new positions and scores
					val nextWinCount = countWins(newPlayerOnePosition, newPlayerTwoPosition, newPlayerOneScore, newPlayerTwoScore, maxScore)
					winCount[0] += nextWinCount[0]
					winCount[1] += nextWinCount[1]
				}
			}

			winCountCache[key] = winCount
			return winCount
		}
	}

	private data class GameResult(
		val diceRolls: Int,
		val playerOneScore: Int,
		val playerTwoScore: Int,
	)

}