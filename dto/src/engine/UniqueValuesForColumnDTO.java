package engine;

import sheet.cell.api.EffectiveValue;

import java.util.HashSet;
import java.util.Set;

public class UniqueValuesForColumnDTO {

    private Set<String> uniqueValues= new HashSet<String>();

    public UniqueValuesForColumnDTO(Set<EffectiveValue> uniqueValues) {
        uniqueValues.forEach(effectiveValue -> this.uniqueValues.add(FormattedValuePrinter.formatValue(effectiveValue)));
    }
    public Set<String> getUniqueValues() {
        return uniqueValues;
    }

}
