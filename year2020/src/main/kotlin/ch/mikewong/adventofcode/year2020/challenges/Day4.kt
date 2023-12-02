package ch.mikewong.adventofcode.year2020.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.allDigits

class Day4 : Day<Int, Int>(2020, 4, "Passport Processing") {

	private val passports by lazy { inputGroups.map { it.joinToString(" ") }.map { Passport(it) } }

	override fun partOne(): Int {
		return passports.count { it.hasAllFields() }
	}

	override fun partTwo(): Int {
		return passports.count { it.isValid() }
	}

	data class Passport(val data: String) {
		companion object {
			private val mandatoryFields = listOf("byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid")
			private val possibleEyeColors = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
		}

		private val fields = data.split(" ").associate { field ->
			val (key, value) = field.split(":")
			key to value
		}

		fun hasAllFields() = mandatoryFields.all { data.contains("$it:") }

		@Suppress("RedundantIf")
		fun isValid(): Boolean {
			if (!hasAllFields()) return false

			// byr (Birth Year) - four digits; at least 1920 and at most 2002.
			if (!isWithinRange(fields.getValue("byr").toIntOrNull(), 1920, 2002)) return false

			// iyr (Issue Year) - four digits; at least 2010 and at most 2020.
			if (!isWithinRange(fields.getValue("iyr").toIntOrNull(), 2010, 2020)) return false

			// eyr (Expiration Year) - four digits; at least 2020 and at most 2030.
			if (!isWithinRange(fields.getValue("eyr").toIntOrNull(), 2020, 2030)) return false

			// hgt (Height) - a number followed by either cm or in:
			// 		If cm, the number must be at least 150 and at most 193.
			// 		If in, the number must be at least 59 and at most 76.
			val hgt = fields.getValue("hgt")
			when {
				hgt.endsWith("cm") -> if (!isWithinRange(hgt.allDigits().toIntOrNull(), 150, 193)) return false
				hgt.endsWith("in") -> if (!isWithinRange(hgt.allDigits().toIntOrNull(), 59, 76)) return false
				else -> return false
			}

			// hcl (Hair Color) - a # followed by exactly six characters 0-9 or a-f.
			val hcl = fields.getValue("hcl")
			if (!hcl.startsWith("#") || hcl.drop(1).toIntOrNull(16) == null) return false

			// ecl (Eye Color) - exactly one of: amb blu brn gry grn hzl oth.
			val ecl = fields.getValue("ecl")
			if (ecl !in possibleEyeColors) return false

			// pid (Passport ID) - a nine-digit number, including leading zeroes.
			val pid = fields.getValue("pid")
			if (pid.length != 9) return false

			// cid (Country ID) - ignored, missing or not.
			return true
		}

		private fun isWithinRange(intField: Int?, min: Int, max: Int): Boolean {
			return intField != null && intField in min..max
		}
	}

}