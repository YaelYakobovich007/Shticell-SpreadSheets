package expression.impl.logic;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import parse.BinaryExpressionParser;
import parse.ExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class BIGGER extends BinaryExpression {
    public static final ExpressionParser<BIGGER> PARSER;

    public BIGGER(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
        this.functionName="BIGGER";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue value1, EffectiveValue value2)
    {
        Boolean result;
        try {
            result = value1.extractValueWithExpectation(Double.class) >= value2.extractValueWithExpectation(Double.class);
        } catch (IllegalArgumentException e) {
            return new EffectiveValueImpl(CellType.BOOLEAN, null);
        }
        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    static {
        PARSER = new BinaryExpressionParser<>("BIGGER", BIGGER.class);
    }

    @Override
    public CellType getResultType(){
        return CellType.BOOLEAN;
    }

}
