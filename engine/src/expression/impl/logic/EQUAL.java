package expression.impl.logic;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import parse.BinaryExpressionParser;
import parse.ExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class EQUAL extends BinaryExpression{
    public static final ExpressionParser<EQUAL> PARSER;

    public EQUAL(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
        this.functionName="EQUAL";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue value1, EffectiveValue value2)
    {
        Boolean result;
        if (value1.getCellType()== value2.getCellType())
        {
            result=(value1.getValue().equals(value2.getValue()));
            return new EffectiveValueImpl(CellType.BOOLEAN, result);

        }
        return new EffectiveValueImpl(CellType.BOOLEAN, null);
    }

    static {
        PARSER = new BinaryExpressionParser<>("EQUAL", EQUAL.class);
    }

    @Override
    public CellType getResultType(){
        return CellType.BOOLEAN;
    }
}
