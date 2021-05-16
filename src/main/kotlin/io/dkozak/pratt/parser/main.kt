package io.dkozak.pratt.parser

fun main() {
    // Function call.
    test("a()", "a()")
    test("a(b)", "a(b)")
    test("a(b, c)", "a(b, c)")
    test("a(b)(c)", "a(b)(c)")
    test("a(b) + c(d)", "(a(b) + c(d))")
    test("a(b ? c : d, e + f)", "a((b ? c : d), (e + f))")

    // Unary precedence.

    // Unary precedence.
    test("~!-+a", "(~(!(-(+a))))")
    test("a!!!", "(((a!)!)!)")

    // Unary and binary predecence.
    test("-a * b", "((-a) * b)")
    test("!a + b", "((!a) + b)")
    test("~a ^ b", "((~a) ^ b)")
    test("-a!", "(-(a!))")
    test("!a!", "(!(a!))")


    // Binary precedence.
    test("a = b + c * d ^ e - f / g", "(a = ((b + (c * (d ^ e))) - (f / g)))")

    // Binary associativity.
    test("a = b = c", "(a = (b = c))")
    test("a + b - c", "((a + b) - c)")
    test("a * b / c", "((a * b) / c)")
    test("a ^ b ^ c", "(a ^ (b ^ c))")

    // Conditional operator.
    test("a ? b : c ? d : e", "(a ? b : (c ? d : e))")
    test("a ? b ? c : d : e", "(a ? (b ? c : d) : e)")
    test("a + b ? c * d : e / f", "((a + b) ? (c * d) : (e / f))")


    // Grouping.
    test("a + (b + c) + d", "((a + (b + c)) + d)")
    test("a ^ (b + c)", "(a ^ (b + c))")
    test("(!a)!", "((!a)!)")


    // Show the results.
    if (failed == 0) {
        println("Passed all $passed tests.")
    } else {
        println("----")
        println("Failed $failed out of ${failed + passed} tests.")
    }
}

var passed = 0
var failed = 0

fun test(source: String, expected: String) {
    val lexer = Lexer(source)
    val parser = BantamParser(lexer)

    try {
        val result = parser.parseExpression()
        val builder = StringBuilder()
        result.print(builder)
        val actual = builder.toString()
        println("$source => $actual")

        if (expected == actual) {
            passed++
        } else {
            failed++
            println("[FAIL] Expected: $expected")
            println("         Actual: $actual")
        }

    } catch (ex: ParseException) {
        failed++
        println("[FAIL] Expected: $expected")
        println("          Error: ${ex.message}")
    }
}

private fun Expression.print(builder: StringBuilder) {
    when (this) {
        is AssignExpression -> {
            builder.append('(')
                .append(this.variable)
                .append(" = ")
            this.value.print(builder)
            builder.append(')')
        }
        is CallExpression -> {
            this.function.print(builder)
            builder.append('(')
            for (i in this.arguments.indices) {
                this.arguments[i].print(builder)
                if (i != this.arguments.size - 1) builder.append(", ")
            }
            builder.append(')')
        }
        is ConditionalExpression -> {
            builder.append("(")
            this.condition.print(builder)
            builder.append(" ? ")
            this.thenArm.print(builder)
            builder.append(" : ")
            this.elseArm.print(builder)
            builder.append(")")
        }
        is BinaryOperatorExpression -> {
            builder.append("(")
            this.left.print(builder)
            builder.append(" ").append(this.type.punctuator).append(" ")
            this.right.print(builder)
            builder.append(")")
        }
        is PostfixExpression -> {
            builder.append("(")
            this.expr.print(builder)
            builder.append(this.type.punctuator).append(")")
        }
        is PrefixExpression -> {
            builder.append("(").append(this.type.punctuator)
            this.expr.print(builder)
            builder.append(")")
        }
        is VariableExpression -> {
            builder.append(this.name)
        }
    }
}
