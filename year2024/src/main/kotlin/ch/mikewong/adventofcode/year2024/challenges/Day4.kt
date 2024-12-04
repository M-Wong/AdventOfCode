package ch.mikewong.adventofcode.year2024.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.toCharGrid
import ch.mikewong.adventofcode.common.models.Direction
import ch.mikewong.adventofcode.common.models.Point

class Day4 : Day<Int, Int>(2024, 4, "Ceres Search") {

	private val characters by lazy { inputLines.toCharGrid() }

	override fun partOne(): Int {
		val xCharacters = characters.filterValues { it == 'X' }
		return xCharacters.map { (point, _) ->
			Direction.entries.minus(Direction.CENTER).count { direction ->
				findXmas(point, direction, "MAS")
			}
		}.sum()
	}

	override fun partTwo(): Int {
		val aCharacters = characters.filterValues { it == 'A' }

		return aCharacters.map { (point, _) ->
			val northWest = characters[point.move(Direction.NORTH_WEST)]
			val northEast = characters[point.move(Direction.NORTH_EAST)]
			val southWest = characters[point.move(Direction.SOUTH_WEST)]
			val southEast = characters[point.move(Direction.SOUTH_EAST)]

			if ((northWest == 'M' && southEast == 'S') || (northWest == 'S' && southEast == 'M')) {
				if ((northEast == 'M' && southWest == 'S') || (northEast == 'S' && southWest == 'M')) {
					1
				} else {
					0
				}
			} else {
				0
			}
		}.sum()
	}

	private fun findXmas(start: Point, direction: Direction, remaining: String): Boolean {
		if (remaining.isEmpty()) return true

		val next = start.move(direction)
		return if (characters[next] == remaining.first()) {
			findXmas(next, direction, remaining.drop(1))
		} else {
			false
		}
	}

}