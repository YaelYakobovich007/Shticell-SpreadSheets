package parse;

import exception.ErrorMessageUtil;
import expression.impl.BinaryExpression;
import expression.api.Expression;
import java.util.*;

public  class BinaryExpressionParser<T extends BinaryExpression> extends FunctionParser<T> {
    public BinaryExpressionParser(String functionName, Class<T> expressionClass) {
        super(functionName, expressionClass);
    }

    @Override
    protected T createExpressionFromArguments(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new RuntimeException(ErrorMessageUtil.generateIncorrectNumberOfArgumentsMessage(functionName,2,arguments.size()));
        }
        Expression left = ExpressionFactory.createExpression(arguments.get(0));
        Expression right = ExpressionFactory.createExpression(arguments.get(1));

        return createBinaryExpression(left, right);
    }

    protected  T createBinaryExpression(Expression left, Expression right)
    {
        try {
            return expressionClass.getConstructor(Expression.class, Expression.class).newInstance(left, right);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + expressionClass.getSimpleName(), e);
        }
    }
}

