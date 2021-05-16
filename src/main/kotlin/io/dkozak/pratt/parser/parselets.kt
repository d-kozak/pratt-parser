package io.dkozak.pratt.parser


class AssignParselet : InfixParselet {
    override val precedence: Int = Precedence.ASSIGNMENT

    override fun parse(parser: Parser, left: Expression, token: Token): Expression {
        val right = parser.parseExpression(precedence - 1) // -1 for right assoc
        validate(left is VariableExpression) { "The left-hand side of an assignment must be a name." }
        val name = (left as VariableExpression).name
        return AssignExpression(name, right)
    }
}

class BinaryOperatorParselet(override val precedence: Int, private val isRightAssoc: Boolean) : InfixParselet {
    override fun parse(parser: Parser, left: Expression, token: Token): Expression {
        val right = parser.parseExpression(precedence - if (isRightAssoc) 1 else 0)
        return BinaryOperatorExpression(left, right, token.type)
    }
}

class CallParselet : InfixParselet {
    override val precedence: Int = Precedence.CALL

    override fun parse(parser: Parser, left: Expression, token: Token): Expression {
        val args = mutableListOf<Expression>()
        if (!parser.match(TokenType.RIGHT_PAREN)) {
            do {
                args.add(parser.parseExpression())
            } while (parser.match(TokenType.COMMA))
            parser.consume(TokenType.RIGHT_PAREN)
        }
        return CallExpression(left, args)
    }
}

class ConditionalParselet : InfixParselet {
    override val precedence: Int = Precedence.CONDITIONAL

    override fun parse(parser: Parser, left: Expression, token: Token): Expression {
        val thenArm = parser.parseExpression()
        parser.consume(TokenType.COLON)
        val elseArm = parser.parseExpression(precedence - 1)
        return ConditionalExpression(left, thenArm, elseArm)
    }
}

class PostfixOperationParselet(override val precedence: Int) : InfixParselet {
    override fun parse(parser: Parser, left: Expression, token: Token): Expression = PostfixExpression(left, token.type)
}

class PrefixOperatiionParselet(val precedence: Int) : PrefixParselet {
    override fun parse(parser: Parser, token: Token): Expression {
        val right = parser.parseExpression(precedence)
        return PrefixExpression(token.type, right)
    }
}

class GroupParselet : PrefixParselet {
    override fun parse(parser: Parser, token: Token): Expression {
        val inner = parser.parseExpression()
        parser.consume(TokenType.RIGHT_PAREN)
        return inner
    }
}

class VariableParselet : PrefixParselet {
    override fun parse(parser: Parser, token: Token): Expression = VariableExpression(token.text)
}

