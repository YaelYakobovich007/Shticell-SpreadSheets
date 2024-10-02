package parse;

import expression.api.Expression;

public interface ExpressionParser<T extends Expression> {
    T parse(String input);
}
