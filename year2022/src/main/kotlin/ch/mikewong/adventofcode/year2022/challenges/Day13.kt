package ch.mikewong.adventofcode.year2022.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.asInts
import ch.mikewong.adventofcode.common.extensions.product
import kotlin.math.sign

class Day13 : Day<Int, Int>(2022, 13, "Distress Signal") {

	override fun partOne(): Int {
		return inputGroups.mapIndexed { index, group ->
			// Parse each signal and compare left with right and either return its index (one-based) or zero to sum up every correctly ordered index
			val leftSignal = parseSignal(group.first())
			val rightSignal = parseSignal(group.last())

			val comparison = leftSignal.compareTo(rightSignal)

			if (comparison < 0) index + 1 else 0
		}.sum()
	}

	override fun partTwo(): Int {
		// Parse all signals, add the divider packets to the list and sort it
		val dividerPackets = listOf(
			parseSignal("[[2]]"),
			parseSignal("[[6]]"),
		)
		val signals = inputGroups.flatten().map { parseSignal(it) } + dividerPackets
		val sortedSignals = signals.sorted()

		return dividerPackets.map { sortedSignals.indexOf(it) + 1 }.product()
	}

	private fun parseSignal(signal: String): SignalValue {
		// If signal does not nest anymore, return it as a single value
		if (signal[0] != '[') return SignalValue.Single(signal.toInt())

		var nestingLevel = 0
		var lastIndex = 1

		val values = mutableListOf<SignalValue>()
		signal.forEachIndexed { index, c ->
			when (c) {
				'[' -> nestingLevel++
				']' -> nestingLevel--
				',' -> if (nestingLevel == 1) {
					// Parse the singal substring between the last index and the current index and add it to the values
					values.add(parseSignal(signal.substring(lastIndex, index)))
					lastIndex = index + 1
				}
			}
		}

		// Sanity check because we should never reach this point with a nesting level > 0
		if (nestingLevel != 0) throw IllegalStateException("Should be at nesting level 0 by now (signal: $signal)")

		if (lastIndex < signal.length - 1) {
			// If the last index we looked at is not yet the end of the signal, parse the substring between
			values.add(parseSignal(signal.substring(lastIndex, signal.length - 1)))
		}

		return SignalValue.Multiple(values)
	}

	private sealed class SignalValue: Comparable<SignalValue> {
		data class Single(val value: Int) : SignalValue() {
			override fun toString() = value.toString()
		}
		data class Multiple(val values: List<SignalValue>) : SignalValue() {
			override fun toString() = "[" + values.joinToString(",") + "]"
		}

		override fun compareTo(other: SignalValue): Int {
			when {
				this is Single && other is Single -> {
					// Two single values just compare their integer values
					return this.value - other.value
				}
				this is Multiple && other is Multiple -> {
					// Two lists first compare each value in their list with each other (zipping them together)
					this.values.zip(other.values).forEach { (a, b) ->
						val comparison = a.compareTo(b)

						// If two list values are not equal, return their comparison value
						if (comparison != 0) return comparison
					}

					// If both lists zipped together are equal, check their sizes to see if one of them is longer
					return this.values.size - other.values.size
				}
				this is Single -> {
					// If this is a single value and other a list, wrap this in a list and then compare them
					return Multiple(listOf(this)).compareTo(other)
				}
				other is Single -> {
					// If this is a list and other is a single value, wrap other in a list and then compare them
					return this.compareTo(Multiple(listOf(other)))
				}
				else -> throw IllegalStateException("Unknown value types for $this and $other")
			}
		}
	}
}