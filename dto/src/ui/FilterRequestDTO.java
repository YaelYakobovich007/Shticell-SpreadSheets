package ui;

import java.util.List;
import java.util.Map;

public class FilterRequestDTO {
    private String fromCoordinate;
    private String toCoordinate;
    private Map<Character,List<String>> selectedColumns2UniqueValues;

    public FilterRequestDTO(String fromCoordinate, String toCoordinate, Map<Character,List<String>> selectedColumns2UniqueValues) {
        this.fromCoordinate = fromCoordinate;
        this.toCoordinate = toCoordinate;
        this.selectedColumns2UniqueValues = selectedColumns2UniqueValues;
    }
    public String getFromCoordinate() {
        return fromCoordinate;
    }
    public String getToCoordinate() {
        return toCoordinate;
    }

    public Map<Character,List<String>> getSelectedColumns2UniqueValues() {
        return selectedColumns2UniqueValues;
    }
}
