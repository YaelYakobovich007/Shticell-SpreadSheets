package parse;

import exception.ErrorMessageUtil;
import expression.api.Expression;
import java.util.List;

public class UnaryExpressionParser<T extends Expression> extends FunctionParser<T> {

    public UnaryExpressionParser(String functionName, Class<T> expressionClass) {
        super(functionName,expressionClass);
    }

    @Override
    protected T createExpressionFromArguments(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new RuntimeException(ErrorMessageUtil.generateIncorrectNumberOfArgumentsMessage(functionName,1,arguments.size()));
        }
        Expression argument = ExpressionFactory.createExpression(arguments.getFirst());

        return createUnaryExpression(argument);
    }

    protected  T createUnaryExpression(Expression argument)
    {
        try {
            return expressionClass.getConstructor(Expression.class).newInstance(argument);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + expressionClass.getSimpleName(), e);
        }
    }
}
