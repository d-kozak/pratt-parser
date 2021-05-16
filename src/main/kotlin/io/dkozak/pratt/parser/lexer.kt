package io.dkozak.pratt.parser

class Lexer(private val input: String) : Iterator<Token> {

    private var i = 0

    private val punctuators = TokenType.values()
        .filter { it.punctuator != null }
        .associateBy { it.punctuator!! }

    override fun next(): Token {
        while (i < input.length) {
            val c = input[i++]
            val x = punctuators[c]
            if (x != null) {
                return Token(x, c.toString())
            } else if (c.isLetter()) {
                val from = i - 1
                while (i < input.length && input[i].isLetter()) i++
                return Token(TokenType.VARIABLE, input.substring(from, i))
            } else {
                // fallthrough
            }
        }
        return Token(TokenType.EOF, "")
    }

    override fun hasNext(): Boolean = true
}