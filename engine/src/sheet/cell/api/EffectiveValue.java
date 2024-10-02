package sheet.cell.api;

import java.io.Serializable;

public interface EffectiveValue extends Serializable {
    CellType getCellType();
    Object getValue();
    <T> T extractValueWithExpectation(Class<T> type);

}
