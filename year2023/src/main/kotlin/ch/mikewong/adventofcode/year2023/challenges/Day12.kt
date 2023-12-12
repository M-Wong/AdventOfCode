package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allInts
import ch.mikewong.adventofcode.common.extensions.repeat

class Day12 : Day<Int, Long>(2023, 12, "Hot Springs") {

	private val conditionRecords = inputLines.map { line ->
		val (conditions, groups) = line.split(" ")
		ConditionRecord(conditions, groups.allInts())
	}

	/**
	 * Cache to memorize already calculated arrangements.
	 * Key: <remaining spring conditions string> to <number of remaining damaged spring groups>
 	 * Value: count of possible arrangements
	 */
	private val arrangementCache = mutableMapOf<Pair<String, Int>, Long>()

	override fun partOne(): Int {
		return conditionRecords.sumOf {
			// Clear the arrangement cache in between records because records are unrelated to each other
			arrangementCache.clear()
			countPossibleArrangements(it.conditions, it.damagedSpringGroups).toInt()
		}
	}

	override fun partTwo(): Long {
		val unfoldedConditionRecords = conditionRecords.map { record ->
			// Unfold the springs by repeating the patterns 5 times (and adding an unknown spring in between)
			val unfoldedConditions = record.conditions.repeat(5).joinToString("?")
			val unfoldedDamagedSpringGroups = record.damagedSpringGroups.repeat(5).flatten().toList()
			ConditionRecord(unfoldedConditions, unfoldedDamagedSpringGroups)
		}
		return unfoldedConditionRecords.sumOf {
			// Clear the arrangement cache in between records because records are unrelated to each other
			arrangementCache.clear()
			countPossibleArrangements(it.conditions, it.damagedSpringGroups)
		}
	}

	/**
	 * Count all possible arrangements of conditions for a [remainingSpringConditions] string and a list of [remainingDamagedSpringGroups]
	 */
	private fun countPossibleArrangements(remainingSpringConditions: String, remainingDamagedSpringGroups: List<Int>): Long {
		// Check how many damaged springs should be contained in the remaining condition string
		val damagedSpringCount = remainingDamagedSpringGroups.sum()

		// Count how many damaged springs are currently fixed in the remaining condition string
		val currentDamagedSprings = remainingSpringConditions.count { it == '#' }

		// Count how many damaged springs there can possibly be in the remaining condition string
		val possibleDamagedSprings = remainingSpringConditions.count { it != '.' }

		if (damagedSpringCount == 0 && currentDamagedSprings == 0) {
			// There are no more damaged spring groups left and the remaining condition string doesn't contain any more damaged springs. So we found a possible condition
			return 1
		}

		if (currentDamagedSprings > damagedSpringCount || damagedSpringCount > possibleDamagedSprings) {
			// With the current remaining condition string, it's not possible to create a match anymore
			return 0
		}

		// Check the next spring in the remaining condition string
		when (remainingSpringConditions.first()) {
			'#' -> {
				// Check if the conditions could match the next group of damaged springs
				val sizeOfNextGroup = remainingDamagedSpringGroups.first()
				val conditionsMatchNextGroup = remainingSpringConditions.matchesDamagedSpringGroup(sizeOfNextGroup)

				return if (conditionsMatchNextGroup) {
					// If the conditions could match, skip the springs in this group and check for the next group
					countPossibleArrangements(
						remainingSpringConditions.drop(sizeOfNextGroup + 1),
						remainingDamagedSpringGroups.drop(1)
					)
				} else {
					0
				}
			}
			'?' -> {
				// If this exact remaining spring conditions string was checked before (for the same number of remaining damaged spring groups), return the cached value instead of counting again
				val key = remainingSpringConditions to remainingDamagedSpringGroups.size
				arrangementCache[key]?.let { return it }

				// Check if the conditions could match the next group of damaged springs
				val sizeOfNextGroup = remainingDamagedSpringGroups.first()
				val conditionsMatchNextGroup = remainingSpringConditions.matchesDamagedSpringGroup(sizeOfNextGroup)

				// Since the first character of the remaining conditions can be either type of spring, count from the next spring (treating this spring as operational)
				var sum = countPossibleArrangements(remainingSpringConditions.substring(1), remainingDamagedSpringGroups)

				if (conditionsMatchNextGroup) {
					// If the conditions could match, skip until the next damaged spring group and count from there (treating this spring as non-operational)
					sum += countPossibleArrangements(
						remainingSpringConditions.drop(sizeOfNextGroup + 1),
						remainingDamagedSpringGroups.drop(1)
					)
				}

				return sum.also { arrangementCache[key] = it }
			}
			else -> {
				val nextNonOperationalSpring = remainingSpringConditions.indexOfFirst { it != '.' }.takeIf { it > 0 } ?: 1
				return countPossibleArrangements(
					remainingSpringConditions.substring(nextNonOperationalSpring),
					remainingDamagedSpringGroups
				)
			}
		}
	}

	/**
	 * Check if the current group could match the next group of damaged springs:
	 * - The next number of springs (equal to the size of the next group) must not be operational
	 * - The next spring after this group must either not exist (end of string) or note be damaged
	 */
	private fun String.matchesDamagedSpringGroup(groupSize: Int): Boolean {
		val hasExactGroupMatch = this.take(groupSize).all { it != '.' }
		val isGroupIsolated = groupSize >= this.length || this[groupSize] != '#'
		return hasExactGroupMatch && isGroupIsolated
	}

	private data class ConditionRecord(val conditions: String, val damagedSpringGroups: List<Int>)

}