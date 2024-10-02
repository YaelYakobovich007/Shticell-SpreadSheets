package parse;

import exception.ErrorMessageUtil;
import expression.api.Expression;
import range.Range;
import java.util.List;

public class SUMParser<T extends Expression> extends FunctionParser<T> {
    public SUMParser(String functionName, Class<T> expressionClass) {
        super(functionName, expressionClass);
    }

    @Override
    protected T createExpressionFromArguments(List<String> arguments) {
        Range range;
        if (arguments.size() != 1) {
            throw new RuntimeException(ErrorMessageUtil.generateIncorrectNumberOfArgumentsMessage(functionName,1,arguments.size()));
        }
        try {
            range = ExpressionFactory.currentSheet.getRange(arguments.getFirst());
            ExpressionFactory.currentSheet.addNewRangeInUse(arguments.getFirst());

        }catch (RuntimeException e) {
            range=null;
        }
        try{
            return expressionClass.getConstructor(Range.class).newInstance(range);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + expressionClass.getSimpleName(), e);
        }
    }
}
