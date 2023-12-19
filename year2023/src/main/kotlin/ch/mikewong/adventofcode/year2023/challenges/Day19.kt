package ch.mikewong.adventofcode.year2023.challenges

import ch.mikewong.adventofcode.common.challenges.Day
import ch.mikewong.adventofcode.common.extensions.*

class Day19 : Day<Int, Long>(2023, 19, "Aplenty") {

	private val machineParts = inputGroups.last().map { line ->
		val categories = line.trim('{', '}').split(",").associate { category ->
			val (property, value) = category.split("=")
			property to value.toInt()
		}
		MachinePart(categories)
	}

	private val workflows = inputGroups.first().associate { line ->
		val name = line.substringUntil("{")
		val rules = line.substringBetween("{", "}").split(",").map { ruleString ->
			if (ruleString.contains(":")) {
				val (check, next) = ruleString.split(":")
				if (check.contains("<")) {
					val (property, value) = check.split("<")
					Rule.Check(property, max = value.toInt(), nextWorkflow = next)
				} else {
					val (property, value) = check.split(">")
					Rule.Check(property, min = value.toInt(), nextWorkflow = next)
				}
			} else {
				Rule.Throughput(ruleString)
			}
		}
		name to rules
	}

	override fun partOne(): Int {
		return machineParts.filter { it.isAcceptable("in") }.sumOf { it.sum() }
	}

	override fun partTwo(): Long {
		val acceptableRanges = mutableMapOf(
			"x" to 1..4000L,
			"m" to 1..4000L,
			"a" to 1..4000L,
			"s" to 1..4000L,
		)

		return countAcceptableRanges(acceptableRanges, "in")
	}

	private fun countAcceptableRanges(ranges: Map<String, LongRange>, workflowName: String): Long {
		if (workflowName == "R") {
			// The workflow to check is an immediate rejection
			return 0
		}

		if (workflowName == "A") {
			// The workflow to check is an immediate acceptance, return the total number of all acceptable ranges
			val rangeSizes = ranges.values.map { it.size() }
			return rangeSizes.product()
		}

		// The total number of acceptable ranges
		var sumOfAcceptableRanges = 0L

		// The current ranges after applying some of the rules within this workflow
		var currentRanges = ranges

		val rules = workflows.getValue(workflowName)
		rules.forEach { rule ->
			when (rule) {
				is Rule.Check -> {
					// Check rules splits the current range of one category into an accepted and rejected range
					val currentRange = currentRanges.getValue(rule.category)
					val (acceptedRange, rejectedRange) = if (rule.max >= currentRange.last) {
						// The rule range extends further than the current category range
						val accepted = (rule.min + 1)..currentRange.last
						val rejected = currentRange.first..rule.min
						accepted to rejected
					} else {
						// The rule range starts before the current category range
						val accepted = currentRange.first until rule.max
						val rejected = rule.max..currentRange.last
						accepted to rejected
					}

					// Check the next workflow with the new accepted ranges
					if (!acceptedRange.isEmpty()) {
						val acceptedRanges = currentRanges.set(rule.category, acceptedRange)
						sumOfAcceptableRanges += countAcceptableRanges(acceptedRanges, rule.nextWorkflow)
					}

					// Update the remaining ranges to the rejected ranges and continue to the next rule in this workflow
					val rejectedRanges = currentRanges.set(rule.category, rejectedRange)
					currentRanges = rejectedRanges
				}
				is Rule.Throughput -> {
					// Throughput rules (which are always the last one) check the next workflow with the current ranges
					sumOfAcceptableRanges += countAcceptableRanges(currentRanges, rule.nextWorkflow)
				}
			}
		}

		return sumOfAcceptableRanges
	}

	/**
	 * Checks if [this] machine part is acceptable by the rules within [workFlowName]. This checks recursively until the part is either found acceptable or is rejected
	 */
	private fun MachinePart.isAcceptable(workFlowName: String): Boolean {
		when (workFlowName) {
			"R" -> return false
			"A" -> return true
			else -> {
				val rules = workflows.getValue(workFlowName)
				rules.forEach { rule ->
					when (rule) {
						is Rule.Check -> {
							// For check rules, continue with the next workflow if the machine part category falls inside the rule range, otherwise continue to the next rule
							if (this.categories[rule.category] in rule.range) {
								return this.isAcceptable(rule.nextWorkflow)
							}
						}

						// For throughput rules, directly check the next workflow for acceptance
						is Rule.Throughput -> return this.isAcceptable(rule.nextWorkflow)
					}
				}
				throw IllegalStateException("At least one rule should have applied")
			}
		}
	}

	private data class MachinePart(val categories: Map<String, Int>) {
		fun sum() = categories.values.sum()
	}

	private sealed interface Rule {
		/**
		 * A rule that checks a specific [category] if it is within the [range] specified by the [min] and [max] values.
		 * If the check passes, continue with the [nextWorkflow]
		 */
		data class Check(val category: String, val min: Int = 0, val max: Int = 4000, val nextWorkflow: String) : Rule {
			val range get() = min..max
		}

		/**
		 * A rule that does no check but instead only passes through to the [nextWorkflow]
		 */
		data class Throughput(val nextWorkflow: String) : Rule
	}
}