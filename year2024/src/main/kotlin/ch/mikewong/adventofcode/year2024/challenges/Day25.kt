package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day25 : Day<Int, Int>(2024, 25, "Code Chronicle") {

	private val schematics by lazy { readInput() }

	override fun partOne(): Int {
		return schematics.findUniqueCombinations()
	}

	override fun partTwo(): Int {
		return 0
	}

	private fun readInput(): Schematics {
		val locks = mutableListOf<Lock>()
		val keys = mutableListOf<Key>()

		inputGroups.forEach { schema ->
			val schemaLength = schema.first().length

			if (schema.first().startsWith("#")) {
				// Schema is a lock
				val heights = (0 until schemaLength).map { idx ->
					schema.count { it[idx] == '#' }
				}
				locks.add(Lock(schema.size, heights))
			} else if (schema.last().startsWith("#")) {
				// Schema is a key
				val heights = (0 until schemaLength).map { idx ->
					schema.count { it[idx] == '#' }
				}
				keys.add(Key(heights))
			} else {
				throw IllegalArgumentException("Invalid schema")
			}
		}

		return Schematics(locks, keys)
	}

	private data class Schematics(val locks: List<Lock>, val keys: List<Key>) {
		fun findUniqueCombinations(): Int {
			return locks.sumOf { lock ->
				keys.count { key ->
					lock.heights.zip(key.heights).all { (lockHeight, keyHeight) ->
						lockHeight + keyHeight <= lock.availableSpace
					}
				}
			}
		}
	}

	private data class Lock(val availableSpace: Int, val heights: List<Int>)

	private data class Key(val heights: List<Int>)

}