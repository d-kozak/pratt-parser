package io.dkozak.pratt.parser

abstract class Parser(private val lexer: Lexer) {
    private val prefixParselets = mutableMapOf<TokenType, PrefixParselet>()
    private val infixParselets = mutableMapOf<TokenType, InfixParselet>()
    private val tokenBuffer = mutableListOf<Token>()

    fun register(type: TokenType, parselet: PrefixParselet) {
        prefixParselets[type] = parselet
    }

    fun register(type: TokenType, parselet: InfixParselet) {
        infixParselets[type] = parselet
    }

    fun parseExpression(precedence: Int): Expression {
        var token = consume()
        val prefix = prefixParselets[token.type] ?: throw ParseException("Could not parse '${token.text}'")
        var left = prefix.parse(this, token)
        while (precedence < getPrecedence()) {
            token = consume()
            val infix = infixParselets.getValue(token.type)
            left = infix.parse(this, left, token)
        }
        return left
    }

    fun parseExpression() = parseExpression(0)

    fun match(expected: TokenType): Boolean {
        val actual = lookAhead(0)
        if (actual.type != expected) return false
        consume()
        return true
    }

    fun consume(expected: TokenType): Token {
        val actual = lookAhead(0)
        validate(actual.type == expected) { "Expected token $expected and found ${actual.type}" }
        return consume()
    }

    fun consume(): Token {
        lookAhead(0)
        return tokenBuffer.removeAt(0)
    }

    private fun lookAhead(dist: Int): Token {
        while (dist >= tokenBuffer.size)
            tokenBuffer.add(lexer.next())
        return tokenBuffer[dist]
    }

    private fun getPrecedence() = infixParselets[lookAhead(0).type]?.precedence ?: 0

}

interface PrefixParselet {
    fun parse(parser: Parser, token: Token): Expression
}

interface InfixParselet {
    val precedence: Int
    fun parse(parser: Parser, left: Expression, token: Token): Expression
}

fun validate(condition: Boolean, lazyMsg: () -> String) {
    // todo contracts
    if (!condition) throw ParseException(lazyMsg())
}

class ParseException(message: String) : RuntimeException(message)