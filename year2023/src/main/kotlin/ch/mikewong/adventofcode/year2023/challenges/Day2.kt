package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day2 : Day<Int, Int>(2023, 2, "Cube Conundrum") {

	private val games = inputLines.map { line ->
		val (game, sets) = line.split(":")
		val (_, gameId) = game.split(" ")
		val cubeSets = sets.split(";").map { set ->
			val cubes = set.trim().split(", ").associate { cube ->
				val (amount, color) = cube.split(" ")
				color to amount.toInt()
			}
			CubeSet(
				cubes.getOrDefault("red", 0),
				cubes.getOrDefault("green", 0),
				cubes.getOrDefault("blue", 0),
			)
		}
		Game(gameId.toInt(), cubeSets)
	}

	override fun partOne(): Int {
		val maxCubeSet = CubeSet(12, 13, 14)
		return games.filter { game ->
			game.cubeSets.all { it.isPossibleWith(maxCubeSet) }
		}.sumOf { it.id }
	}

	override fun partTwo(): Int {
		return games.sumOf { game ->
			val minRed = game.cubeSets.maxOf { it.red }
			val minGreen = game.cubeSets.maxOf { it.green }
			val minBlue = game.cubeSets.maxOf { it.blue }
			minRed * minGreen * minBlue
		}
	}

	private data class Game(val id: Int, val cubeSets: List<CubeSet>)

	private data class CubeSet(val red: Int, val green: Int, val blue: Int) {
		fun isPossibleWith(maxCubeSet: CubeSet) = red <= maxCubeSet.red && green <= maxCubeSet.green && blue <= maxCubeSet.blue
	}

}