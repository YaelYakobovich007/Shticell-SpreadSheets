package expression.api;

import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;

public interface Expression
{
    EffectiveValue evaluate();
    CellType getResultType();
}
