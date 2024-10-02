package expression.impl;

import expression.api.Expression;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class Text implements Expression
{
    private final String value;
    private final CellType type;

    public Text(String text)
    {
        this.value=text;
        this.type= CellType.STRING;
    }

    @Override
    public EffectiveValue evaluate() {
        return new EffectiveValueImpl(type, value);
    }

    @Override
    public CellType getResultType() {
        return type;
    }

    @Override
    public String toString() {return value;}
}
