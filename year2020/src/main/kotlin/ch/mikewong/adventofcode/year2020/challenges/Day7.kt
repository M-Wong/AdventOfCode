package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allNumbers
import ch.mikewong.adventofcode.common.extensions.substringUntil

class Day7 : Day<Int, Int>(2020, 7, "Handy Haversacks") {

	private val startingBagColor = "shiny gold"

	private val bagMapping = inputLines.associate { line ->
		val (container, content) = line.split("contain")
		val containerColor = container.substringUntil("bags").trim()
		val contentColors = content.split(",").mapNotNull { part ->
			val count = part.allNumbers().singleOrNull()
			count?.let {
				val color =part.substringUntil("bag")
					.filterNot { c -> c.isDigit() }
					.trim()

				color to it
			}
		}.toMap()
		containerColor to contentColors
	}

	private val bagColorsContainingShinyGold = mutableMapOf<String, Boolean>()

	override fun partOne(): Int {
		bagMapping.keys.minus(startingBagColor).forEach { bagColor ->
			// Iterate all bag colors and check them if they can contain a shiny gold bag
			bagColorsContainingShinyGold[bagColor] = canBagContainShinyGold(bagColor)
		}

		return bagColorsContainingShinyGold.count { (_, possible) -> possible }
	}

	override fun partTwo(): Int {
		return countNumberOfBagsWithin(startingBagColor)
	}

	private fun canBagContainShinyGold(bagColor: String): Boolean {
		// Early return in case this bag color was already processed
		if (bagColorsContainingShinyGold.containsKey(bagColor)) return bagColorsContainingShinyGold.getValue(bagColor)

		val contentColors = bagMapping.getValue(bagColor)
		return if (contentColors.containsKey(startingBagColor)) {
			// If this bag can directly contain shiny gold bags, return true
			true
		} else {
			// Otherwise check any of the content bag colors recursively
			contentColors.any { contentColor ->
				canBagContainShinyGold(contentColor.key).also { bagColorsContainingShinyGold[contentColor.key] = it }
			}
		}
	}

	private fun countNumberOfBagsWithin(bagColor: String): Int {
		val bagContents = bagMapping.getValue(bagColor)
		return if (bagContents.isNotEmpty()) {
			// Recursively count the number of bags within this bag
			bagContents.map { (color, amount) -> amount * countNumberOfBagsWithin(color) }.sum() + bagContents.values.sum()
		} else {
			0
		}
	}

}