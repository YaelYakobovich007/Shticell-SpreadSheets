package expression.impl;

import expression.api.Expression;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;
import sheet.cell.impl.EffectiveValueImpl;
import java.text.NumberFormat;
import java.util.Locale;

public class Number implements Expression
{
    private final double value;
    private final CellType type;

    public Number(double value){
        this.value = value;
        this.type = CellType.NUMERIC;
    }

    @Override
    public EffectiveValue evaluate() {

        return new EffectiveValueImpl(type, value);
    }

    @Override
    public CellType getResultType(){
        return type;
    }

    public String toString()
    {
        if (value % 1 == 0) {
            return NumberFormat.getIntegerInstance(Locale.US).format(value);
        }
        else
        {
            return String.format(Locale.US, "%,.2f", value);
        }
    }
}
