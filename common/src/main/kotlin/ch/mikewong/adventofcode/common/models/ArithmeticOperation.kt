package ch.mikewong.adventofcode.common.models

enum class ArithmeticOperation(val sign: String) {
	PLUS("+"),
	MINUS("-"),
	MULTIPLY("*"),
	DIVIDE("/");

	companion object {
		fun fromString(s: String) = entries.first { s == it.sign }
	}

	fun apply(first: Long, second: Long) = when (this) {
		PLUS -> first + second
		MINUS -> first - second
		MULTIPLY -> first * second
		DIVIDE -> first / second
	}
}