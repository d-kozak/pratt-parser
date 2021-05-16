package io.dkozak.pratt.parser

class BantamParser(lexer: Lexer) : Parser(lexer) {
    init {
        register(TokenType.VARIABLE, VariableParselet())
        register(TokenType.ASSIGN, AssignParselet())
        register(TokenType.QUESTION, ConditionalParselet())
        register(TokenType.LEFT_PAREN, CallParselet()) // infix
        register(TokenType.LEFT_PAREN, GroupParselet()) // prefix

        prefix(TokenType.PLUS, Precedence.PREFIX)
        prefix(TokenType.MINUS, Precedence.PREFIX)
        prefix(TokenType.TILDE, Precedence.PREFIX)
        prefix(TokenType.BANG, Precedence.PREFIX)

        postfix(TokenType.BANG, Precedence.POSTFIX)

        infixLeft(TokenType.PLUS, Precedence.SUM)
        infixLeft(TokenType.MINUS, Precedence.SUM)
        infixLeft(TokenType.ASTERIX, Precedence.PRODUCT)
        infixLeft(TokenType.SLASH, Precedence.PRODUCT)
        infixRight(TokenType.CARET, Precedence.EXPONENT)
    }

    private fun infixLeft(type: TokenType, precedence: Int) {
        register(type, BinaryOperatorParselet(precedence, false))
    }

    private fun infixRight(type: TokenType, precedence: Int) {
        register(type, BinaryOperatorParselet(precedence, true))
    }

    private fun postfix(type: TokenType, precedence: Int) {
        register(type, PostfixOperationParselet(precedence))
    }

    private fun prefix(type: TokenType, precedence: Int) {
        register(type, PrefixOperatiionParselet(precedence))
    }
}