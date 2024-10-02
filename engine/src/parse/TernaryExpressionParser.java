package parse;

import exception.ErrorMessageUtil;
import expression.api.Expression;
import expression.impl.TernaryExpression;
import java.util.List;

public class TernaryExpressionParser<T extends TernaryExpression> extends FunctionParser<T>{

    public TernaryExpressionParser(String functionName, Class<T> expressionClass) {
        super(functionName,expressionClass);
    }

    @Override
    protected T createExpressionFromArguments(List<String> arguments) {
        if (arguments.size() != 3) {
            throw new RuntimeException(ErrorMessageUtil.generateIncorrectNumberOfArgumentsMessage(functionName,3, arguments.size()));
        }
        Expression argument1 = ExpressionFactory.createExpression(arguments.get(0));
        Expression argument2 = ExpressionFactory.createExpression(arguments.get(1));
        Expression argument3 = ExpressionFactory.createExpression(arguments.get(2));

        return createTernaryExpression(argument1, argument2,argument3);
    }

    protected  T createTernaryExpression(Expression argument1, Expression argument2,Expression argument3)
    {
        try {
            return expressionClass.getConstructor(Expression.class, Expression.class, Expression.class)
                    .newInstance(argument1, argument2, argument3);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + expressionClass.getSimpleName(), e);
        }

    }
}
