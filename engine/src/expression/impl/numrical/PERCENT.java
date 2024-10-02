package expression.impl.numrical;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import parse.BinaryExpressionParser;
import parse.ExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class PERCENT extends BinaryExpression{
    public static final ExpressionParser<PERCENT> PARSER;

    public PERCENT(Expression expression1, Expression  expression2)
    {
        super(expression1, expression2);
        this.functionName="PERCENT";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue value1, EffectiveValue value2) {
        double result;
        double part, whole;

        try {
            part=value1.extractValueWithExpectation(Double.class);
            whole =value2.extractValueWithExpectation(Double.class);
            result = (part * whole) / 100;

        } catch (IllegalArgumentException e) {
            result= Double.NaN;
        }
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    static {
        PARSER = new BinaryExpressionParser<>("PERCENT", PERCENT.class);
    }

    @Override
    public CellType getResultType() {
        return  CellType.NUMERIC;
    }
}
