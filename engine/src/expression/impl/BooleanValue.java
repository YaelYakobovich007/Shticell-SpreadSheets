package expression.impl;

import expression.api.Expression;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;

public class BooleanValue implements Expression {
    private final Boolean value;
    private final CellType type;

    public BooleanValue(Boolean bool) {

        this.value = bool;
        this.type = CellType.BOOLEAN;
    }

    @Override
    public EffectiveValue evaluate() {
        return new EffectiveValueImpl(type, value);
    }

    @Override
    public CellType getResultType(){
        return type;
    }

    @Override
    public String toString()
    {
        return value.toString().toUpperCase();
    }
}
