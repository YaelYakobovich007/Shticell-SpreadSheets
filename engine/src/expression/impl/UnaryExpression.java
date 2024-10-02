package expression.impl;
import expression.api.Expression;
import sheet.cell.api.EffectiveValue;

public abstract class UnaryExpression implements Expression {
    public String functionName;
    private final Expression expression;

    public UnaryExpression(Expression expression1)
    {
        this.expression = expression1;
    }

    @Override
    public EffectiveValue evaluate()
    {
        return evaluate(expression.evaluate());
    }

    public abstract EffectiveValue evaluate(EffectiveValue evaluate1);
}
