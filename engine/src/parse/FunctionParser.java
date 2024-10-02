package parse;

import expression.api.Expression;
import java.util.ArrayList;
import java.util.List;

public abstract class FunctionParser<T extends Expression> implements ExpressionParser<T> {
    protected final String functionName;
    protected final Class<T> expressionClass;

    public FunctionParser(String functionName,Class<T> expressionClass) {
        this.functionName = functionName;
        this.expressionClass = expressionClass;
    }

    @Override
    public T parse(String input) {
        if (!input.trim().toUpperCase().startsWith("{" + functionName + ",") || !input.endsWith("}")) {
            throw new IllegalArgumentException("Invalid input for " + functionName + " function");
        }
        String inner = input.substring(input.indexOf(',') + 1, input.lastIndexOf('}'));
        List<String> parts = splitTopLevelArguments(inner);
        return createExpressionFromArguments(parts);
    }

    private  List<String> splitTopLevelArguments(String input) {
        List<String> result = new ArrayList<>();
        int braceCount = 0;
        int lastSplit = 0;

        for (int i = 0; i < input.length(); i++){
            char ch = input.charAt(i);
            if (ch == '{') {
                braceCount++;
            } else if (ch == '}') {
                braceCount--;
            } else if (ch == ',' && braceCount == 0) {
                result.add(input.substring(lastSplit, i));
                lastSplit = i + 1;
            }
        }
        result.add(input.substring(lastSplit));
        return result;
    }

    protected abstract T createExpressionFromArguments(List<String> arguments);
}


