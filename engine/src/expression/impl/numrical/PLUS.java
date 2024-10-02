package expression.impl.numrical;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import parse.*;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class PLUS extends BinaryExpression {
    public static final ExpressionParser<PLUS> PARSER;

    public PLUS(Expression expression1, Expression  expression2) {
        super(expression1, expression2);
        this.functionName="PLUS";
    }

        @Override
        public EffectiveValue evaluate(EffectiveValue value1, EffectiveValue value2) {
            double result;
            try {
                result = value1.extractValueWithExpectation(Double.class) + value2.extractValueWithExpectation(Double.class);
            } catch (IllegalArgumentException e) {

                result= Double.NaN;

            }
            return new EffectiveValueImpl(CellType.NUMERIC, result);
        }

    static {
        PARSER = new BinaryExpressionParser<>("PLUS", PLUS.class);
    }

    @Override
    public CellType getResultType() {
        return  CellType.NUMERIC;
    }

}
