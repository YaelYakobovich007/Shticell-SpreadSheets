package engine;

import sheet.cell.api.EffectiveValue;
import java.util.Set;

public class UniqueValuesForColumnDTO {

    private Set<EffectiveValue> uniqueValues;

    public UniqueValuesForColumnDTO(Set<EffectiveValue> uniqueValues) {
        this.uniqueValues = uniqueValues;
    }
    public Set<EffectiveValue> getUniqueValues() {
        return uniqueValues;
    }

}
