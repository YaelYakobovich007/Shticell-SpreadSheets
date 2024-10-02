package expression.impl.string;

import expression.api.Expression;
import expression.impl.BinaryExpression;
import parse.BinaryExpressionParser;
import parse.ExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class CONCAT extends BinaryExpression {
    public static final ExpressionParser<CONCAT> PARSER;

    public CONCAT(Expression expression1, Expression expression2)
    {
        super(expression1, expression2);
        this.functionName="CONCAT";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue value1, EffectiveValue value2)
    {
        String result;
        try {
            String stringValue1=value1.extractValueWithExpectation(String.class);
            String stringValue2=value2.extractValueWithExpectation(String.class);

            if ("!UNDEFINED!".equals(stringValue1) || "!UNDEFINED!".equals(stringValue2)) {
                result = "!UNDEFINED!";
            } else {
                result = stringValue1 + stringValue2;
            }
        } catch (IllegalArgumentException e) {
            result = "!UNDEFINED!";
        }
        return new EffectiveValueImpl(CellType.STRING, result);
    }

    @Override
    public CellType getResultType() {
        return CellType.STRING;
    }

    static {
        PARSER = new BinaryExpressionParser<>("CONCAT", CONCAT.class);
    }
}
