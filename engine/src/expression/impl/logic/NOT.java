package expression.impl.logic;

import expression.api.Expression;
import expression.impl.UnaryExpression;
import parse.ExpressionParser;
import parse.UnaryExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class NOT extends UnaryExpression {
    public static final ExpressionParser<NOT> PARSER;

    public NOT(Expression expression1)
    {
        super(expression1);
        this.functionName="NOT";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue value1)
    {
        Boolean result;
        try {
            result = !value1.extractValueWithExpectation(Boolean.class);

        } catch (IllegalArgumentException e) {
            return new EffectiveValueImpl(CellType.BOOLEAN, null);
        }
        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    @Override
    public CellType getResultType() {
        return CellType.BOOLEAN;
    }

    static {
        PARSER = new UnaryExpressionParser<>("NOT", NOT.class);
    }
}
