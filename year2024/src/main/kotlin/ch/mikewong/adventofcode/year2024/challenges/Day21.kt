package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allDigits
import ch.mikewong.adventofcode.common.extensions.permutations
import ch.mikewong.adventofcode.common.extensions.repeat
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day21 : Day<Long, Long>(2024, 21, "Keypad Conundrum") {

	private val codes by lazy { inputLines }

	private val sequenceCache = mutableMapOf<Pair<String, Int>, Long>()

	// 248108
	override fun partOne(): Long {
		sequenceCache.clear()
		val shortestSequences = codes.associateWith { findShortestSequence(it, 2) }

		return shortestSequences.map { (code, sequenceLength) ->
			val numericPart = code.allDigits().toInt()
			sequenceLength * numericPart
		}.sum()
	}

	// 303836969158972
	override fun partTwo(): Long {
		sequenceCache.clear()
		val shortestSequences = codes.associateWith { findShortestSequence(it, 25) }

		return shortestSequences.map { (code, sequenceLength) ->
			val numericPart = code.allDigits().toInt()
			sequenceLength * numericPart
		}.sum()
	}

	/**
	 * Finds the shortest sequence for a specific [targetSequence] on [currentRobot]
	 * This goes from the first robot (which is the one typing on the numeric keypad) up the chain until [robotCount] is reached
	 * Essentially the last robot is us pressing the inputs on the directional keypad
	 */
	private fun findShortestSequence(targetSequence: String, robotCount: Int, currentRobot: Int = 0): Long {
		return sequenceCache.getOrPut(targetSequence to currentRobot) {
			// Determine the keypad of this robot
			val keypad = if (currentRobot == 0) KeypadType.NUMERIC else KeypadType.DIRECTION

			// Every input sequence starts at the key 'A' (either because it's the initial position or because it was last pressed)
			var currentKey = 'A'
			var sequenceLength = 0L

			// Iterate over each character in the sequence to calculate the length of the sequence required to press that target sequence
			targetSequence.forEach { nextKey ->
				// Get all possible move sets from the current key to the next key
				val possibleMoveSets = getPossibleMoveSets(currentKey, nextKey, keypad)
				currentKey = nextKey

				if (currentRobot == robotCount) {
					// If this is the last robot, just take the shortest sequence
					sequenceLength += possibleMoveSets.minOfOrNull { it.length } ?: 0
				} else {
					// If this is an intermediary robot, recursively find all possible sequences
					val possibleSequences = possibleMoveSets.map { moveSet ->
						findShortestSequence(moveSet, robotCount, currentRobot + 1)
					}

					// Take the shortest sequence from all possible sequences
					sequenceLength += possibleSequences.min()
				}
			}

			sequenceLength
		}
	}

	// This is the original implementation, which returned the exact sequence, which was nice for testing/debugging and worked well for part 1
	// However, part 2 immediately ran into memory issues, because the strings became so long, they crashed IntelliJ during debugging...
//	private fun findShortestSequence(targetSequence: String, currentRobot: Int, lastRobot: Int): String {
//		return sequenceCache.getOrPut(targetSequence to currentRobot) {
//			val keypad = if (currentRobot == 0) KeypadType.NUMERIC else KeypadType.DIRECTION
//
//			var current = 'A'
//			buildString {
//				targetSequence.forEach { next ->
//					val possibleMoveSets = getPossibleMoveSets(current, next, keypad)
//
//					if (currentRobot == lastRobot) {
//						val shortestSequence = possibleMoveSets.minByOrNull { it.length } ?: ""
//						append(shortestSequence)
//					} else {
//						val possibleSequences = possibleMoveSets.map { moveSet ->
//							findShortestSequence(moveSet, currentRobot + 1, lastRobot)
//						}
//
//						val shortestSequence = possibleSequences.minByOrNull { it.length }
//							?: possibleMoveSets.minByOrNull { it.length }
//							?: ""
//
//						append(shortestSequence)
//					}
//
//					current = next
//				}
//			}
//		}
//	}

	/**
	 * Constructs all possible move sets [from] one key [to] another key on a specific [keypad]
	 */
	private fun getPossibleMoveSets(from: Char, to: Char, keypad: KeypadType): List<String> {
		// If it's the same key, the move set only needs to press the button
		if (from == to) return listOf("A")

		// Get the coordinates of the two keys, then calculate the delta in x and y coordinates
		val fromPoint = keypad.getPoint(from)
		val toPoint = keypad.getPoint(to)

		val dx = (toPoint.x - fromPoint.x).toInt()
		val dy = (toPoint.y - fromPoint.y).toInt()

		// To get from one key to another, these are the moves that need to be taken
		val moves = buildList {
			// Vertical moves
			when {
				dx > 0 -> addAll(Direction.SOUTH.repeat(dx))
				dx < 0 -> addAll(Direction.NORTH.repeat(-dx))
			}

			// Horizontal moves
			when {
				dy > 0 -> addAll(Direction.EAST.repeat(dy))
				dy < 0 -> addAll(Direction.WEST.repeat(-dy))
			}
		}

		// Calculate all possible move sets by getting unique permutations of the moves
		val allPossibleMoveSets = moves.toList().permutations().distinct()

		// Filter out move sets that are invalid due to passing through an empty space
		val allValidMoveSets = allPossibleMoveSets.filter { moveSet ->
			var current = fromPoint
			moveSet.all {
				current = current.move(it)
				keypad.contains(current)
			}
		}

		// Map the directional move sets back to a sequence string
		return allValidMoveSets.map { path ->
			path.joinToString("") { it.char().toString() } + 'A'
		}
	}

	/**
	 * An enum representing the two keypad types, with their mappings of keys to coordinates
	 */
	private enum class KeypadType(private val mapping: Map<Char, Point>) {
		NUMERIC(
			mapOf(
				'7' to Point(0, 0),
				'8' to Point(0, 1),
				'9' to Point(0, 2),
				'4' to Point(1, 0),
				'5' to Point(1, 1),
				'6' to Point(1, 2),
				'1' to Point(2, 0),
				'2' to Point(2, 1),
				'3' to Point(2, 2),
				'0' to Point(3, 1),
				'A' to Point(3, 2)
			)
		),
		DIRECTION(
			mapOf(
				'^' to Point(0, 1),
				'A' to Point(0, 2),
				'<' to Point(1, 0),
				'v' to Point(1, 1),
				'>' to Point(1, 2)
			)
		);

		fun getPoint(char: Char) = mapping.getValue(char)

		fun contains(point: Point) = point in mapping.values
	}

}