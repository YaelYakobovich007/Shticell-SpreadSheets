package expression.impl.numrical;

import expression.api.Expression;
import expression.impl.UnaryExpression;
import parse.ExpressionParser;
import parse.UnaryExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class ABS extends UnaryExpression {
    public static final ExpressionParser<ABS> PARSER;

    public ABS(Expression expression1)
    {
        super(expression1);
        this.functionName="ABS";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue value1)
    {
        double result;
        try {
            result = Math.abs(value1.extractValueWithExpectation(Double.class));

        } catch (IllegalArgumentException e) {

            result = Double.NaN;

        }
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    @Override
    public CellType getResultType() {
        return CellType.NUMERIC;
    }

    static {
        PARSER = new UnaryExpressionParser<>("ABS", ABS.class);

    }
}
