package expression.impl.numrical;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import parse.BinaryExpressionParser;
import parse.ExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class TIMES extends BinaryExpression {
    public static final ExpressionParser<TIMES> PARSER;

    public TIMES(Expression expression1, Expression  expression2)
    {
        super(expression1, expression2);
        this.functionName="TIMES";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue value1, EffectiveValue value2)
    {
        double result;
        try {
            result = value1.extractValueWithExpectation(Double.class) * value2.extractValueWithExpectation(Double.class);
        } catch (IllegalArgumentException e) {
            result= Double.NaN;
        }
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    static {
        PARSER = new BinaryExpressionParser<>("TIMES", TIMES.class);
    }

    @Override
    public CellType getResultType() {
        return CellType.NUMERIC;
    }
}
