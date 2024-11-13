package ui;

import java.util.List;

public class SortRequestDTO {
    private  final String fromRange;
    private  final String toRange;
    private  final List<Character> selectedColumns;

    public SortRequestDTO(String fromRange, String toRange, List<Character> selectedColumns) {
        this.fromRange = fromRange;
        this.toRange = toRange;
        this.selectedColumns = selectedColumns;
    }

    public String getFromRange() {
        return fromRange;
    }
    public String getToRange() {
        return toRange;

    }
    public List<Character> getSelectedColumns() {
        return selectedColumns;
    }
}
