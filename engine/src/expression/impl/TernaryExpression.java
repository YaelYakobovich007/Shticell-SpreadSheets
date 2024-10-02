package expression.impl;

import expression.api.Expression;
import sheet.cell.api.EffectiveValue;

public abstract class TernaryExpression implements Expression {
    public String functionName;
    private final Expression expression1;
    private final Expression expression2;
    private final Expression expression3;

    public TernaryExpression(Expression expression1, Expression expression2,Expression expression3) {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.expression3 = expression3;
    }

    @Override
    public EffectiveValue evaluate() {
        return evaluate(expression1.evaluate(), expression2.evaluate(), expression3.evaluate());
    }

    public abstract EffectiveValue evaluate(EffectiveValue evaluate1, EffectiveValue evaluate2, EffectiveValue evaluate3);

}
