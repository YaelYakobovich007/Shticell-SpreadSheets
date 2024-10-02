package expression.impl.string;

import expression.api.Expression;
import expression.impl.TernaryExpression;
import parse.ExpressionParser;
import parse.TernaryExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class SUB extends TernaryExpression {
    public static final ExpressionParser<SUB> PARSER;

    public SUB(Expression expression1, Expression  expression2, Expression expression3)
    {
        super(expression1, expression2, expression3);
        this.functionName="SUB";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue source, EffectiveValue startIndex, EffectiveValue endIndex)
    {
        int start, end;
        String sourceString,result;
        try {
            start =startIndex.extractValueWithExpectation(Double.class).intValue();
            end =endIndex.extractValueWithExpectation(Double.class).intValue();
            sourceString= source.extractValueWithExpectation(String.class);

            if (sourceString.equals("!UNDEFINED!")){
                result= "!UNDEFINED!";
            }else if (start < 0 || end >= sourceString.length() || start > end){
                result= "!UNDEFINED!";
            } else {
                result = sourceString.substring(start, end + 1);
            }
        } catch (IllegalArgumentException e) {
            result= "!UNDEFINED!";
        }

        return new EffectiveValueImpl(CellType.STRING, result);
    }

    static {
        PARSER = new TernaryExpressionParser<>("SUB", SUB.class);
    }

    @Override
    public CellType getResultType() {
        return CellType.STRING;
    }
}
