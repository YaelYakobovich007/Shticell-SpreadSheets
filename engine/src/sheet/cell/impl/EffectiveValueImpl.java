package sheet.cell.impl;

import parse.ExpressionFactory;
import sheet.cell.api.CellType;
import sheet.cell.api.EffectiveValue;

import java.util.Objects;

public class EffectiveValueImpl implements EffectiveValue {
    private final CellType cellType;
    private final Object value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EffectiveValueImpl that = (EffectiveValueImpl) o;
        return cellType == that.cellType && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellType, value);
    }

    public EffectiveValueImpl(CellType cellType, Object value){
        this.cellType = cellType;
        this.value = value;
    }

    @Override
    public CellType getCellType() {
        return cellType;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public <T> T extractValueWithExpectation(Class<T> type) {
        if (cellType.isAssignableFrom(type)) {
            return type.cast(value);
        } else {
            throw new IllegalArgumentException("Cannot assign value of type " + cellType + " to expected type " + type.getName());
        }
    }

    @Override
    public String toString() {
        return ExpressionFactory.createExpression(value.toString()).toString();
    }
}
