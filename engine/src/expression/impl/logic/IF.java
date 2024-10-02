package expression.impl.logic;

import expression.api.Expression;
import expression.impl.TernaryExpression;
import parse.ExpressionParser;
import parse.TernaryExpressionParser;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class IF extends TernaryExpression {
    public static final ExpressionParser<IF> PARSER;

    public IF(Expression expression1, Expression  expression2, Expression expression3)
    {
        super(expression1, expression2, expression3);
        this.functionName="IF";
    }

    @Override
    public EffectiveValue evaluate(EffectiveValue condition, EffectiveValue thenOption, EffectiveValue elseOption)
    {
        EffectiveValue result;

        if(thenOption.getCellType()!=elseOption.getCellType()){
            throw  new RuntimeException("The product type in then and else is not the same.The IF function expects to receive the same product type in both instances of the condition.");
        }
        try {
            Boolean booleanConditionResult= condition.extractValueWithExpectation(Boolean.class);

            if(booleanConditionResult.equals(Boolean.TRUE)){
                result= thenOption;
            }else{
                result= elseOption;
            }
        } catch (IllegalArgumentException e){
            return new EffectiveValueImpl(CellType.BOOLEAN, null);
        }
        return result;
    }

    static {
        PARSER = new TernaryExpressionParser<>("IF", IF.class);
    }

    @Override
    public CellType getResultType() {
        return CellType.UNKNOWN;
    }

}
