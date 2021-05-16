package io.dkozak.pratt.parser

var nextId = 1


abstract class Node {
    val id = nextId++
}

sealed class Expression : Node()
data class VariableExpression(val name: String) : Expression()
data class PrefixExpression(val type: TokenType, val expr: Expression) : Expression()
data class PostfixExpression(val expr: Expression, val type: TokenType) : Expression()
data class BinaryOperatorExpression(val left: Expression, val right: Expression, val type: TokenType) : Expression()
data class ConditionalExpression(
    val condition: Expression, val thenArm: Expression, val elseArm: Expression
) : Expression()

data class CallExpression(val function: Expression, val arguments: List<Expression>) : Expression()
data class AssignExpression(val variable: String, val value: Expression) : Expression()

data class Token(val type: TokenType, val text: String)

object Precedence {
    const val ASSIGNMENT = 1
    const val CONDITIONAL = 2
    const val SUM = 3
    const val PRODUCT = 4
    const val EXPONENT = 5
    const val PREFIX = 6
    const val POSTFIX = 7
    const val CALL = 8
}

enum class TokenType(val punctuator: Char? = null) {
    LEFT_PAREN('('),
    RIGHT_PAREN(')'),
    COMMA(','),
    ASSIGN('='),
    PLUS('+'),
    MINUS('-'),
    ASTERIX('*'),
    SLASH('/'),
    CARET('^'),
    TILDE('~'),
    BANG('!'),
    QUESTION('?'),
    COLON(':'),
    VARIABLE,
    EOF
}