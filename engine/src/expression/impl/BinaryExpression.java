package expression.impl;

import expression.api.Expression;
import sheet.cell.api.EffectiveValue;

public abstract  class BinaryExpression implements Expression {
    public String functionName;
    private final Expression expression1;
    private final Expression expression2;

    public BinaryExpression(Expression expression1, Expression expression2) {
        this.expression1 = expression1;
        this.expression2 = expression2;
    }

    @Override
    public EffectiveValue evaluate() {
        return evaluate(expression1.evaluate(), expression2.evaluate());
    }

    public abstract EffectiveValue evaluate(EffectiveValue evaluate1, EffectiveValue evaluate2);

}
