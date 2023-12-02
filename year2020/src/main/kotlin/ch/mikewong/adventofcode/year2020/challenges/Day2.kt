package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day

class Day2 : Day<Int, Int>(2020, 2, "Password Philosophy") {

	private val regex = "(\\d+)-(\\d+) (\\w): (\\w+)".toRegex()
	private val passwords = inputLines.map { line ->
		val (_, min, max, char, password) = requireNotNull(regex.matchEntire(line)).groupValues
		PasswordPolicy(char.single(), min.toInt(), max.toInt()) to password
	}

	override fun partOne(): Int {
		return passwords.count { (policy, password) -> policy.hasValidCharacterCount(password) }
	}

	override fun partTwo(): Int {
		return passwords.count { (policy, password) -> policy.hasValidCharacterPositions(password) }
	}

	data class PasswordPolicy(val character: Char, val min: Int, val max: Int) {
		fun hasValidCharacterCount(password: String): Boolean {
			return password.count { it == character } in min..max
		}

		fun hasValidCharacterPositions(password: String): Boolean {
			val first = password[min - 1]
			val second = password[max - 1]
			return first != second && (first == character || second == character)
		}
	}

}