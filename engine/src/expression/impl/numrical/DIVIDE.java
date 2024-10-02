package expression.impl.numrical;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import parse.BinaryExpressionParser;
import parse.ExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class DIVIDE extends BinaryExpression {
    public static final ExpressionParser<DIVIDE> PARSER;

    public DIVIDE(Expression expression1, Expression  expression2)
    {
        super(expression1, expression2);
        this.functionName="DIVIDE";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue value1, EffectiveValue value2)
    {
        double numericValue1;
        double numericValue2;
        double result;

        try {
            numericValue1= value1.extractValueWithExpectation(Double.class);
            numericValue2=  value2.extractValueWithExpectation(Double.class);

            if (numericValue2 == 0){
                return new EffectiveValueImpl(CellType.NUMERIC, Double.NaN);
            }
            result = numericValue1 / numericValue2;

        } catch (IllegalArgumentException e) {
            result= Double.NaN;
        }
        return new EffectiveValueImpl(CellType.NUMERIC, result);
    }

    static {
        PARSER = new BinaryExpressionParser<>("DIVIDE", DIVIDE.class);
    }

    @Override
    public CellType getResultType() {
        return CellType.NUMERIC;
    }
}
