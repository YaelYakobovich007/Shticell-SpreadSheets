package expression.impl.logic;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import parse.BinaryExpressionParser;
import parse.ExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class AND extends BinaryExpression {
    public static final ExpressionParser<AND> PARSER;

    public AND(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
        this.functionName="AND";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue value1, EffectiveValue value2)
    {
        Boolean result;
        try {
            result = value1.extractValueWithExpectation(Boolean.class) && value2.extractValueWithExpectation(Boolean.class);
        } catch (IllegalArgumentException e) {
            return new EffectiveValueImpl(CellType.BOOLEAN, null);
        }
        return new EffectiveValueImpl(CellType.BOOLEAN, result);
    }

    static {
        PARSER = new BinaryExpressionParser<>("AND", AND.class);
    }

    @Override
    public CellType getResultType(){
        return CellType.BOOLEAN;
    }
}
